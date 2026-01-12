# SISTEMA DE LOGIN Y AUTENTICACIÓN - DOCUMENTACIÓN COMPLETA

## ÍNDICE
1. [Arquitectura General](#arquitectura-general)
2. [Tabla de Sesiones](#tabla-de-sesiones)
3. [Sistema de Tokens](#sistema-de-tokens)
4. [Flujo Completo de Login](#flujo-completo-de-login)
5. [Sistema de Autorización con Roles](#sistema-de-autorización-con-roles)
6. [Filtro de Autenticación](#filtro-de-autenticación)
7. [Todas las Clases e Implementaciones](#todas-las-clases-e-implementaciones)

---

## ARQUITECTURA GENERAL

El sistema sigue una arquitectura en capas:

```
┌─────────────────────────────────────────────┐
│         CAPA DE PRESENTACIÓN                │
│  - AuthController (REST endpoints)          │
│  - AuthFilter (Filtro de seguridad)         │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│         CAPA DE DOMINIO                     │
│  - AuthService (Lógica de negocio)          │
│  - ClientService (Gestión de usuarios)      │
│  - PasswordEncryptionService (BCrypt)       │
└──────────────────┬───────────────���──────────┘
                   │
┌──────────────────▼──────────────────────────┐
│         CAPA DE REPOSITORIO                 │
│  - SessionRepository (Interface)            │
│  - ClientRepository (Interface)             │
│  - SessionEntity (Record de dominio)        │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│         CAPA DE PERSISTENCIA                │
│  - SessionJpaDao (DAO JPA)                  │
│  - SessionJpaDaoImpl (Implementación)       │
│  - SessionJpaEntity (Entidad JPA)           │
│  - SessionMapperPersistence (Mapper)        │
└─────────────────────────────────────────────┘
```

---

## TABLA DE SESIONES

### Esquema de Base de Datos

```sql
CREATE TABLE sessions (
    id_session BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_client BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    login_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_session_client FOREIGN KEY (id_client) 
        REFERENCES clients(id_client) ON DELETE CASCADE
);

CREATE INDEX idx_session_token ON sessions(token);
CREATE INDEX idx_session_client ON sessions(id_client);
```

### Propósito de la Tabla

- **Almacena las sesiones activas** de los usuarios autenticados
- **Relaciona tokens con usuarios** mediante `id_client`
- **Registra la fecha de login** para auditoría o expiración
- **DELETE CASCADE**: Si se elimina un cliente, sus sesiones se eliminan automáticamente
- **Token único**: Un mismo token no puede estar duplicado (constraint UNIQUE)
- **Índices**: Optimizan las búsquedas por token y por cliente

---

## SISTEMA DE TOKENS

### ¿Qué es un Token?

Un **token** es un identificador único que se genera cuando un usuario hace login exitosamente. Funciona como una "llave" que el cliente debe enviar en cada petición para demostrar que está autenticado.

### Generación de Tokens

```java
// En AuthServiceImpl.java
@Override
public String createTokenFromUser(ClientDto clientDto) {
    // 1. Generar un UUID aleatorio (único)
    String token = UUID.randomUUID().toString();
    
    // 2. Crear una nueva sesión en la base de datos
    SessionEntity sessionEntity = new SessionEntity(
            null,                    // ID se genera automáticamente
            clientDto.id(),          // ID del cliente
            token,                   // Token generado
            LocalDateTime.now()      // Fecha/hora actual
    );
    
    // 3. Guardar la sesión
    sessionRepository.save(sessionEntity);
    
    // 4. Devolver el token al cliente
    return token;
}
```

**Características:**
- Se usa `UUID.randomUUID()` que genera strings como: `"3f4a8b12-c5d6-4e7f-8901-23a45b6c7d8e"`
- Son **estadísticos únicos** (probabilidad de colisión extremadamente baja)
- Se almacenan en la base de datos vinculados al usuario
- No contienen información del usuario (no son JWT)
- Son **opacos**: solo tienen significado consultando la BD

### Validación de Tokens

```java
// En AuthServiceImpl.java
@Override
public Optional<ClientDto> getUserFromToken(String token) {
    return sessionRepository.findByToken(token)              // 1. Buscar sesión por token
            .flatMap(session -> 
                clientRepository.findById(session.clientId())) // 2. Obtener cliente
            .map(ClientMapper.getInstance()::fromClientEntityToClient)
            .map(ClientMapper.getInstance()::fromClientToClientDto)
            .filter(clientDto -> clientDto.role() == Role.ADMIN); // 3. Solo ADMIN
}

@Override
public Optional<ClientDto> getAnyUserFromToken(String token) {
    return sessionRepository.findByToken(token)
            .flatMap(session -> 
                clientRepository.findById(session.clientId()))
            .map(ClientMapper.getInstance()::fromClientEntityToClient)
            .map(ClientMapper.getInstance()::fromClientToClientDto);
    // Sin filtro de rol - cualquier usuario autenticado
}
```

**Diferencia importante:**
- `getUserFromToken()`: Solo devuelve el usuario si es **ADMIN**
- `getAnyUserFromToken()`: Devuelve cualquier usuario autenticado (USER o ADMIN)

### Eliminación de Tokens (Logout)

```java
@Override
public void deleteToken(String token) {
    sessionRepository.deleteByToken(token);
}
```

Al hacer logout, se elimina el registro de la tabla `sessions`, invalidando el token.

---

## FLUJO COMPLETO DE LOGIN

### 1. Request del Cliente

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "password123"
}
```

### 2. Validación del Request

```java
// LoginRequest.java
public record LoginRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}
```

### 3. Controlador de Autenticación

```java
// AuthController.java
@PostMapping("/login")
public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    // 1. Intentar login con email y password
    Optional<ClientDto> clientOptional = clientService.login(
            loginRequest.email(),
            loginRequest.password()
    );

    // 2. Si las credenciales son incorrectas
    if (clientOptional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
    }

    // 3. Usuario autenticado correctamente
    ClientDto client = clientOptional.get();

    // 4. Generar token y crear sesión
    String token = authService.createTokenFromUser(client);

    // 5. Preparar respuesta
    LoginResponse response = new LoginResponse(
            token,            // Token generado
            client.email(),   // Email del usuario
            client.name(),    // Nombre del usuario
            client.role()     // Rol (USER o ADMIN)
    );

    return ResponseEntity.ok(response); // 200 OK
}
```

### 4. Servicio de Cliente (Validación de Credenciales)

```java
// ClientServiceImpl.java
@Override
public Optional<ClientDto> login(String email, String password) {
    return clientRepository.findByEmail(email)
            // Verificar que la contraseña coincida
            .filter(entity -> passwordEncryptionService.matches(password, entity.password()))
            // Convertir a DTO
            .map(ClientMapper.getInstance()::fromClientEntityToClient)
            .map(ClientMapper.getInstance()::fromClientToClientDto);
}
```

### 5. Encriptación de Contraseñas (BCrypt)

```java
// BCryptPasswordEncryptionService.java
@Override
public boolean matches(String rawPassword, String encodedPassword) {
    if (rawPassword == null || encodedPassword == null) {
        return false;
    }
    try {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    } catch (IllegalArgumentException e) {
        return false;
    }
}

@Override
public String encryptPassword(String rawPassword) {
    if (rawPassword == null || rawPassword.isEmpty()) {
        throw new IllegalArgumentException("La contraseña no puede estar vacía");
    }
    return BCrypt.hashpw(rawPassword, BCrypt.gensalt(WORKLOAD)); // WORKLOAD = 10
}
```

**BCrypt:**
- Algoritmo de hashing seguro para contraseñas
- Incluye "salt" automáticamente (protección contra rainbow tables)
- WORKLOAD = 10 (factor de costo, controla la complejidad del hash)
- Las contraseñas hasheadas empiezan con `$2a$`, `$2b$` o `$2y$`

### 6. Respuesta al Cliente

```json
HTTP/1.1 200 OK
Content-Type: application/json

{
  "token": "3f4a8b12-c5d6-4e7f-8901-23a45b6c7d8e",
  "email": "admin@example.com",
  "name": "Admin User",
  "role": "ADMIN"
}
```

### 7. Uso del Token en Peticiones Posteriores

```http
GET /api/products
Authorization: Bearer 3f4a8b12-c5d6-4e7f-8901-23a45b6c7d8e
```

---

## SISTEMA DE AUTORIZACIÓN CON ROLES

### Enum de Roles

```java
// Role.java
public enum Role {
    USER,
    ADMIN
}
```

### Anotación @RequiresRole

```java
// RequiresRole.java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {
    Role value();
}
```

Esta anotación se coloca en los métodos del controlador para indicar qué rol se requiere:

```java
// Ejemplo de uso en un controlador
@GetMapping("/admin/dashboard")
@RequiresRole(Role.ADMIN)
public ResponseEntity<Dashboard> getAdminDashboard() {
    // Solo accesible para ADMIN
}

@GetMapping("/user/profile")
@RequiresRole(Role.USER)
public ResponseEntity<Profile> getUserProfile() {
    // Solo accesible para USER
}
```

---

## FILTRO DE AUTENTICACIÓN

### AuthFilter - El Guardián de la Aplicación

```java
// AuthFilter.java
@Component
public class AuthFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final RequestMappingHandlerMapping handlerMapping;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. EXCEPCIÓN: Permitir /api/auth/login sin autenticación
        if (request.getRequestURI().equals("/api/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. Obtener el handler del endpoint solicitado
            HandlerExecutionChain handlerChain = handlerMapping.getHandler(request);

            if (handlerChain == null || 
                !(handlerChain.getHandler() instanceof HandlerMethod)) {
                filterChain.doFilter(request, response);
                return;
            }

            HandlerMethod handlerMethod = (HandlerMethod) handlerChain.getHandler();
            
            // 3. Verificar si el endpoint requiere un rol específico
            RequiresRole requiresRole = 
                handlerMethod.getMethodAnnotation(RequiresRole.class);

            // 4. Si no tiene @RequiresRole, es público
            if (requiresRole == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 5. Extraer token del header Authorization
            String token = extractTokenFromHeader(
                request.getHeader("Authorization")
            );

            if (token == null) {
                sendErrorResponse(response, 401, "Token no proporcionado");
                return;
            }

            // 6. Validar token y obtener usuario
            Optional<ClientDto> userOptional = 
                authService.getUserFromToken(token);

            if (userOptional.isEmpty()) {
                sendErrorResponse(response, 401, "Token inválido o expirado");
                return;
            }

            ClientDto user = userOptional.get();

            // 7. Verificar que el usuario tenga el rol requerido
            if (user.role() != requiresRole.value()) {
                sendErrorResponse(response, 403,
                    "Acceso denegado. Se requiere rol: " + requiresRole.value());
                return;
            }

            // 8. Todo OK - continuar con la petición
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            sendErrorResponse(response, 500,
                "Error al procesar la autenticación: " + e.getMessage());
        }
    }

    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Quitar "Bearer "
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, 
                                    int status, 
                                    String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
```

### Configuración del Filtro

```java
// WebConfig.java
@Configuration
public class WebConfig {

    private final AuthFilter authFilter;

    public WebConfig(AuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration() {
        FilterRegistrationBean<AuthFilter> registration = 
            new FilterRegistrationBean<>();
        registration.setFilter(authFilter);
        registration.addUrlPatterns("/api/*");  // Aplicar a todos los endpoints /api/*
        registration.setOrder(1);               // Ejecutar primero
        registration.setName("authFilter");
        return registration;
    }
}
```

### Flujo del Filtro

```
REQUEST → AuthFilter
           │
           ├─→ /api/auth/login? → SÍ → PERMITIR (público)
           │
           ├─→ Handler existe? → NO → PERMITIR (404 manejado por Spring)
           │
           ├─→ Tiene @RequiresRole? → NO → PERMITIR (endpoint público)
           │
           ├─→ Tiene token? → NO → 401 UNAUTHORIZED
           │
           ├─→ Token válido? → NO → 401 UNAUTHORIZED
           │
           ├─→ Rol correcto? → NO → 403 FORBIDDEN
           │
           └─→ TODO OK → CONTINUAR al Controller
```

---

## TODAS LAS CLASES E IMPLEMENTACIONES

### 1. CAPA DE PRESENTACIÓN (Controllers y Filters)

#### AuthController.java
```java
package org.example.storeback.controller;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final ClientService clientService;
    private final AuthService authService;

    // POST /api/auth/login - Login público
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest)

    // POST /api/auth/logout - Cerrar sesión
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader)

    // GET /api/auth/me - Obtener usuario actual (solo ADMIN)
    @GetMapping("/me")
    public ResponseEntity<ClientResponse> getAdminUser(
        @RequestHeader("Authorization") String authHeader)

    // GET /api/auth/me/any - Obtener usuario actual (cualquier rol)
    @GetMapping("/me/any")
    public ResponseEntity<ClientResponse> getCurrentUser(
        @RequestHeader("Authorization") String authHeader)

    private String extractTokenFromHeader(String authHeader)
}
```

**Responsabilidades:**
- Exponer endpoints REST para autenticación
- Validar request bodies con `@Valid`
- Orquestar servicios de dominio
- Construir respuestas HTTP

#### AuthFilter.java
```java
package org.example.storeback.controller.filter;

@Component
public class AuthFilter extends OncePerRequestFilter {
    private final AuthService authService;
    private final RequestMappingHandlerMapping handlerMapping;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain)
    
    private String extractTokenFromHeader(String authHeader)
    private void sendErrorResponse(HttpServletResponse response, int status, String message)
}
```

**Responsabilidades:**
- Interceptar todas las peticiones a `/api/*`
- Verificar tokens de autenticación
- Validar roles mediante `@RequiresRole`
- Bloquear accesos no autorizados

---

### 2. CAPA DE DOMINIO (Services y Models)

#### AuthService.java (Interface)
```java
package org.example.storeback.domain.service;

public interface AuthService {
    String createTokenFromUser(ClientDto clientDto);
    Optional<ClientDto> getUserFromToken(String token);
    Optional<ClientDto> getAnyUserFromToken(String token);
    void deleteToken(String token);
}
```

#### AuthServiceImpl.java
```java
package org.example.storeback.domain.service.impl;

public class AuthServiceImpl implements AuthService {
    private final SessionRepository sessionRepository;
    private final ClientRepository clientRepository;

    @Override
    public String createTokenFromUser(ClientDto clientDto) {
        // Genera UUID, crea SessionEntity, guarda en BD
    }

    @Override
    public Optional<ClientDto> getUserFromToken(String token) {
        // Busca sesión, obtiene cliente, verifica que sea ADMIN
    }

    @Override
    public Optional<ClientDto> getAnyUserFromToken(String token) {
        // Busca sesión, obtiene cliente (cualquier rol)
    }

    @Override
    public void deleteToken(String token) {
        // Elimina sesión de BD
    }
}
```

#### ClientService.java (Interface)
```java
package org.example.storeback.domain.service;

public interface ClientService {
    Optional<ClientDto> findById(Long id);
    Optional<ClientDto> findByEmail(String email);
    ClientDto save(ClientDto clientDto);
    void deleteById(Long id);
    boolean existsByEmail(String email);
    Optional<ClientDto> login(String email, String password);
}
```

#### ClientServiceImpl.java
```java
package org.example.storeback.domain.service.impl;

public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final PasswordEncryptionService passwordEncryptionService;

    @Override
    public Optional<ClientDto> login(String email, String password) {
        // Busca por email, verifica contraseña con BCrypt
    }

    @Override
    public ClientDto save(ClientDto clientDto) {
        // Verifica duplicados, encripta contraseña si es necesaria
    }

    // Otros métodos CRUD
}
```

#### PasswordEncryptionService.java (Interface)
```java
package org.example.storeback.domain.service;

public interface PasswordEncryptionService {
    String encryptPassword(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
```

#### BCryptPasswordEncryptionService.java
```java
package org.example.storeback.Spring;

public class BCryptPasswordEncryptionService implements PasswordEncryptionService {
    private static final int WORKLOAD = 10;

    @Override
    public String encryptPassword(String rawPassword) {
        // Usa BCrypt.hashpw con salt
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        // Usa BCrypt.checkpw
    }
}
```

#### Role.java (Enum)
```java
package org.example.storeback.domain.models;

public enum Role {
    USER,
    ADMIN
}
```

#### RequiresRole.java (Annotation)
```java
package org.example.storeback.domain.validation;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {
    Role value();
}
```

---

### 3. CAPA DE DOMINIO - DTOs y Entities

#### ClientDto.java
```java
package org.example.storeback.domain.service.dto;

public record ClientDto(
        Long id,
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String password,
        String phone,
        Long cartId,
        @NotNull Role role
) {}
```

#### SessionEntity.java (Domain Entity - Record)
```java
package org.example.storeback.domain.repository.entity;

public record SessionEntity(
        Long id,
        Long clientId,
        String token,
        LocalDateTime loginDate
) {}
```

#### ClientEntity.java (Domain Entity - Record)
```java
package org.example.storeback.domain.repository.entity;

public record ClientEntity(
        Long id,
        String name,
        String email,
        String password,
        String phone,
        Long cartId,
        Role role
) {}
```

---

### 4. CAPA DE REPOSITORIO (Interfaces)

#### SessionRepository.java
```java
package org.example.storeback.domain.repository;

public interface SessionRepository {
    Optional<SessionEntity> findByToken(String token);
    SessionEntity save(SessionEntity sessionEntity);
    void deleteByToken(String token);
}
```

#### ClientRepository.java
```java
package org.example.storeback.domain.repository;

public interface ClientRepository {
    Optional<ClientEntity> findById(Long id);
    Optional<ClientEntity> findByEmail(String email);
    ClientEntity save(ClientEntity clientEntity);
    void deleteById(Long id);
    boolean existsByEmail(String email);
}
```

---

### 5. CAPA DE PERSISTENCIA

#### SessionRepositoryImpl.java
```java
package org.example.storeback.persistence.repository;

public class SessionRepositoryImpl implements SessionRepository {
    private final SessionJpaDao sessionJpaDao;

    @Override
    public Optional<SessionEntity> findByToken(String token) {
        return sessionJpaDao.findByToken(token);
    }

    @Override
    public SessionEntity save(SessionEntity sessionEntity) {
        return sessionJpaDao.save(sessionEntity);
    }

    @Override
    public void deleteByToken(String token) {
        sessionJpaDao.deleteByToken(token);
    }
}
```

#### SessionJpaDao.java (Interface)
```java
package org.example.storeback.persistence.dao;

public interface SessionJpaDao {
    Optional<SessionEntity> findByToken(String token);
    SessionEntity save(SessionEntity sessionEntity);
    void deleteByToken(String token);
}
```

#### SessionJpaDaoImpl.java
```java
package org.example.storeback.persistence.dao.jpa.impl;

@Transactional
public class SessionJpaDaoImpl implements SessionJpaDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<SessionEntity> findByToken(String token) {
        // Query JPQL: SELECT s FROM SessionJpaEntity s WHERE s.token = :token
    }

    @Override
    public SessionEntity save(SessionEntity sessionEntity) {
        // Convierte a JPA entity, persist o merge, devuelve domain entity
    }

    @Override
    public void deleteByToken(String token) {
        // Query + entityManager.remove()
    }
}
```

#### SessionJpaEntity.java (JPA Entity)
```java
package org.example.storeback.persistence.dao.jpa.entity;

@Entity
@Table(name = "sessions")
public class SessionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_session")
    private Long id;

    @Column(name = "id_client", nullable = false)
    private Long clientId;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "login_date", nullable = false)
    private LocalDateTime loginDate;

    // Constructor vacío, constructor completo, getters, setters, toString
}
```

---

### 6. MAPPERS

#### SessionMapperPersistence.java
```java
package org.example.storeback.persistence.repository.mapper;

public class SessionMapperPersistence {
    private static SessionMapperPersistence INSTANCE;

    public static SessionMapperPersistence getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionMapperPersistence();
        }
        return INSTANCE;
    }

    public SessionEntity fromSessionJpaEntityToSessionEntity(SessionJpaEntity jpa) {
        // Convierte JPA Entity → Domain Entity (record)
    }

    public SessionJpaEntity fromSessionEntityToSessionJpaEntity(SessionEntity entity) {
        // Convierte Domain Entity (record) → JPA Entity
    }
}
```

#### ClientMapper.java
```java
package org.example.storeback.domain.mappers;

public class ClientMapper {
    private static ClientMapper instance;

    public static ClientMapper getInstance() {
        if (instance == null) {
            instance = new ClientMapper();
        }
        return instance;
    }

    public Client fromClientEntityToClient(ClientEntity clientEntity)
    public ClientEntity fromClientToClientEntity(Client client)
    public Client fromClientDtoToClient(ClientDto clientDto)
    public ClientDto fromClientToClientDto(Client client)
}
```

---

### 7. WEB MODELS (Request/Response)

#### LoginRequest.java
```java
package org.example.storeback.controller.webmodel.request;

public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {}
```

#### LoginResponse.java
```java
package org.example.storeback.controller.webmodel.response;

public record LoginResponse(
        String token,
        String email,
        String name,
        Role role
) {}
```

---

### 8. CONFIGURACIÓN

#### WebConfig.java
```java
package org.example.storeback.Spring;

@Configuration
public class WebConfig {
    private final AuthFilter authFilter;

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration() {
        // Registra el filtro para /api/*
    }
}
```

---

## FLUJOS COMPLETOS CON EJEMPLOS

### FLUJO 1: Usuario hace Login

```
1. Cliente envía POST /api/auth/login
   Body: { "email": "admin@example.com", "password": "pass123" }

2. AuthFilter intercepta
   → URI es /api/auth/login → PERMITIR (excepción pública)

3. AuthController.login() recibe request
   → Valida con @Valid (email formato correcto, campos no vacíos)

4. Llama a clientService.login(email, password)
   → ClientServiceImpl busca en BD por email
   → Encuentra ClientEntity
   → passwordEncryptionService.matches(password, entity.password())
   → BCrypt verifica hash → TRUE
   → Devuelve Optional<ClientDto>

5. AuthController verifica resultado
   → Optional NO está vacío → continuar
   → Llama a authService.createTokenFromUser(clientDto)

6. AuthServiceImpl.createTokenFromUser()
   → Genera UUID: "3f4a8b12-c5d6-4e7f-8901-23a45b6c7d8e"
   → Crea SessionEntity(null, clientId, token, LocalDateTime.now())
   → sessionRepository.save(sessionEntity)

7. SessionRepositoryImpl.save()
   → sessionJpaDao.save(sessionEntity)

8. SessionJpaDaoImpl.save()
   → Convierte SessionEntity → SessionJpaEntity (mapper)
   → entityManager.persist(jpaEntity)
   → BD: INSERT INTO sessions...
   → Devuelve SessionEntity con ID generado

9. AuthController construye LoginResponse
   → LoginResponse(token, email, name, role)
   → ResponseEntity.ok(response)

10. Cliente recibe: 200 OK
    Body: {
      "token": "3f4a8b12-c5d6-4e7f-8901-23a45b6c7d8e",
      "email": "admin@example.com",
      "name": "Admin User",
      "role": "ADMIN"
    }
```

---

### FLUJO 2: Usuario accede a endpoint protegido

```
1. Cliente envía GET /api/products
   Header: Authorization: Bearer 3f4a8b12-c5d6-4e7f-8901-23a45b6c7d8e

2. AuthFilter intercepta
   → URI NO es /api/auth/login → continuar verificación

3. AuthFilter obtiene handler
   → handlerMapping.getHandler(request)
   → Encuentra ProductController.getAllProducts()
   → Método anotado con @RequiresRole(Role.ADMIN)

4. AuthFilter extrae token
   → extractTokenFromHeader("Bearer 3f4a8b12...")
   → token = "3f4a8b12-c5d6-4e7f-8901-23a45b6c7d8e"

5. AuthFilter valida token
   → authService.getUserFromToken(token)

6. AuthServiceImpl.getUserFromToken()
   → sessionRepository.findByToken(token)
   → SessionRepositoryImpl → SessionJpaDaoImpl
   → Query: SELECT * FROM sessions WHERE token = ?
   → Encuentra SessionEntity(id=1, clientId=5, token=..., loginDate=...)
   → clientRepository.findById(5)
   → Encuentra ClientEntity
   → Convierte a ClientDto mediante mappers
   → Verifica: clientDto.role() == Role.ADMIN → TRUE
   → Devuelve Optional<ClientDto>

7. AuthFilter verifica rol
   → user.role() (ADMIN) == requiresRole.value() (ADMIN) → TRUE
   → filterChain.doFilter(request, response) → PERMITIR

8. ProductController.getAllProducts() se ejecuta
   → Devuelve lista de productos

9. Cliente recibe: 200 OK
   Body: [ { productos... } ]
```

---

### FLUJO 3: Usuario con rol incorrecto intenta acceder

```
1. Cliente USER envía GET /api/admin/dashboard
   Header: Authorization: Bearer user-token-xyz

2. AuthFilter intercepta
   → Método anotado con @RequiresRole(Role.ADMIN)

3. AuthFilter valida token
   → authService.getUserFromToken("user-token-xyz")
   → Busca sesión → encuentra usuario con Role.USER
   → filter(clientDto -> clientDto.role() == Role.ADMIN) → FALSE
   → Devuelve Optional.empty()

4. AuthFilter detecta Optional vacío
   → sendErrorResponse(response, 401, "Token inválido o expirado")

5. Cliente recibe: 401 UNAUTHORIZED
   Body: { "error": "Token inválido o expirado" }
```

**NOTA:** En este caso, el filtro devuelve 401 en lugar de 403 porque `getUserFromToken()`
solo devuelve usuarios ADMIN. Si usara `getAnyUserFromToken()`, detectaría el rol incorrecto
y devolvería 403 FORBIDDEN.

---

### FLUJO 4: Logout

```
1. Cliente envía POST /api/auth/logout
   Header: Authorization: Bearer 3f4a8b12-c5d6-4e7f-8901-23a45b6c7d8e

2. AuthFilter intercepta
   → URI NO es /api/auth/login
   → Método logout() NO tiene @RequiresRole → PERMITIR (público)

3. AuthController.logout() recibe request
   → Extrae token del header
   → authService.deleteToken(token)

4. AuthServiceImpl.deleteToken()
   → sessionRepository.deleteByToken(token)

5. SessionRepositoryImpl.deleteByToken()
   → sessionJpaDao.deleteByToken(token)

6. SessionJpaDaoImpl.deleteByToken()
   → Query: SELECT * FROM sessions WHERE token = ?
   → entityManager.remove(sessionJpaEntity)
   → BD: DELETE FROM sessions WHERE token = ?

7. AuthController devuelve
   → ResponseEntity.noContent().build()

8. Cliente recibe: 204 NO CONTENT

9. El token ahora es inválido
   → Futuras peticiones con ese token → 401 UNAUTHORIZED
```

---

## RESUMEN DE ENDPOINTS

| Endpoint | Método | Autenticación | Rol Requerido | Descripción |
|----------|--------|---------------|---------------|-------------|
| `/api/auth/login` | POST | No | - | Login público |
| `/api/auth/logout` | POST | No | - | Cerrar sesión |
| `/api/auth/me` | GET | Sí | ADMIN | Obtener perfil (solo admin) |
| `/api/auth/me/any` | GET | Sí | Cualquiera | Obtener perfil (cualquier usuario) |

---

## CONSIDERACIONES DE SEGURIDAD

### 1. Contraseñas
- **Nunca se almacenan en texto plano**
- **BCrypt con WORKLOAD=10** (balance seguridad/rendimiento)
- **Salt automático** incluido en el hash
- **Formato hash:** `$2a$10$...` (60 caracteres)

### 2. Tokens
- **UUID v4** (128 bits de aleatoriedad)
- **Almacenados en BD** (tokens con estado)
- **Unique constraint** en la columna token
- **No contienen información sensible**
- **Se invalidan al hacer logout**

### 3. Sesiones
- **Vinculadas a usuarios** mediante foreign key
- **Cascada de eliminación** (si se borra usuario, se borran sus sesiones)
- **Índices para optimizar búsquedas**
- **Timestamp de login** para auditoría

### 4. Filtro de Autenticación
- **OncePerRequestFilter** (ejecuta una sola vez por request)
- **Orden 1** (primera prioridad)
- **Aplica a /api/\*** (todos los endpoints API)
- **Excepción explícita** para /api/auth/login

### 5. Manejo de Errores
- **401 Unauthorized:** Token inválido, expirado o no proporcionado
- **403 Forbidden:** Token válido pero rol insuficiente
- **Respuestas JSON** consistentes: `{"error": "mensaje"}`

---

## MEJORAS SUGERIDAS (Opcional para el futuro)

### 1. Expiración de Tokens
```java
// Agregar campo a SessionEntity
public record SessionEntity(
    Long id,
    Long clientId,
    String token,
    LocalDateTime loginDate,
    LocalDateTime expirationDate  // NUEVO
) {}

// En AuthServiceImpl
@Override
public Optional<ClientDto> getUserFromToken(String token) {
    return sessionRepository.findByToken(token)
        .filter(session -> session.expirationDate().isAfter(LocalDateTime.now()))
        // ...resto del código
}
```

### 2. Refresh Tokens
- Token de acceso de corta duración (15 min)
- Refresh token de larga duración (7 días)
- Endpoint `/api/auth/refresh` para renovar

### 3. Múltiples Sesiones por Usuario
- Permitir login desde múltiples dispositivos
- Tabla sessions permite múltiples registros por clientId
- Endpoint `/api/auth/sessions` para listar sesiones activas
- Endpoint `/api/auth/revoke/{sessionId}` para cerrar sesión específica


### 5. Logging de Auditoría
```java
logger.info("Login successful: user={}, ip={}", email, request.getRemoteAddr());
logger.warn("Failed login attempt: email={}, ip={}", email, request.getRemoteAddr());
```

### 6. CORS y HTTPS
- Configurar CORS para frontend
- Forzar HTTPS en producción
- Secure cookies para tokens (si se usan cookies)

---

## CONCLUSIÓN

Este sistema de login implementa una arquitectura robusta y escalable con:

✅ **Separación de capas** (presentación, dominio, persistencia)  
✅ **Encriptación BCrypt** para contraseñas  
✅ **Tokens opacos** almacenados en BD
✅ **Validación en múltiples niveles**  
✅ **Mappers para conversión** entre capas  
✅ **DTOs, Entities y Records** bien definidos

**Todas las clases están documentadas** y el flujo es completo desde la petición HTTP hasta la base de datos y viceversa.

---

## CÓMO IMPLEMENTAR EN OTRO BACKEND

### Paso 1: Base de Datos
```sql
CREATE TABLE sessions (
    id_session BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_client BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    login_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_client) REFERENCES clients(id_client) ON DELETE CASCADE
);
CREATE INDEX idx_session_token ON sessions(token);
```

### Paso 2: Entidades JPA
- SessionJpaEntity (tabla sessions)
- ClientJpaEntity (tabla clients con password hasheada)

### Paso 3: DAOs
- SessionJpaDao + Implementación (persist, findByToken, deleteByToken)
- ClientJpaDao + Implementación (findByEmail)

### Paso 4: Servicios
- BCryptPasswordEncryptionService (encriptar/verificar passwords)
- ClientService con método login(email, password)
- AuthService con createToken, getUserFromToken, deleteToken

### Paso 5: Controlador
- AuthController con endpoints /login, /logout, /me

### Paso 6: Filtro
- AuthFilter que intercepta peticiones, verifica tokens y roles

### Paso 7: Configuración
- WebConfig registrando el filtro
- Anotación @RequiresRole para proteger endpoints

**Con esta documentación, cualquier desarrollador puede replicar el sistema completo.**

