-- Crear tabla de clientes
CREATE TABLE clients (
    id_client BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido1 VARCHAR(100) NOT NULL,
    apellido2 VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    api_token VARCHAR(255) UNIQUE
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_clients_username ON clients(username);
CREATE INDEX idx_clients_dni ON clients(dni);
CREATE INDEX idx_clients_api_token ON clients(api_token);
