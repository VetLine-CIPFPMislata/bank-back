-- Crear tabla de cuentas bancarias
CREATE TABLE bank_accounts (
    id_bank_account BIGINT AUTO_INCREMENT PRIMARY KEY,
    iban VARCHAR(34) NOT NULL UNIQUE,
    saldo DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    client_id BIGINT NOT NULL,
    CONSTRAINT fk_bank_account_client FOREIGN KEY (client_id)
        REFERENCES clients(id_client) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índices para mejorar rendimiento
CREATE INDEX idx_bank_accounts_client ON bank_accounts(client_id);
CREATE INDEX idx_bank_accounts_iban ON bank_accounts(iban);
