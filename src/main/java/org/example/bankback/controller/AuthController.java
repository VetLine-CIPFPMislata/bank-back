package org.example.bankback.controller;


import org.example.bankback.controller.webmodel.request.LoginRequest;
import org.example.bankback.controller.webmodel.response.LoginResponse;
import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.service.AuthService;
import org.example.bankback.domain.service.ClientService;
import org.example.bankback.domain.service.PasswordEncryptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ClientService clientService;
    private final AuthService authService;
    private final PasswordEncryptionService passwordEncryptionService;

    public AuthController(ClientService clientService, AuthService authService, PasswordEncryptionService passwordEncryptionService) {
        this.clientService = clientService;
        this.authService = authService;
        this.passwordEncryptionService = passwordEncryptionService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        Optional<Client> clientOptional = clientService.login(
                loginRequest.username(),
                loginRequest.password()
        );


        if (clientOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Client client = clientOptional.get();


        String token = authService.createTokenFromUser(client);


        LoginResponse response = new LoginResponse(
                token,
                client.getUsername(),
                client.getNombre() + " " + client.getApellido1()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = extractTokenFromHeader(authHeader);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        authService.deleteToken(token);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Client> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = extractTokenFromHeader(authHeader);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Optional<Client> userOptional = authService.getUserFromToken(token);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(userOptional.get());
    }

    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
