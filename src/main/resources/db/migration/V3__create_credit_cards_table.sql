-- Crear tabla de tarjetas de crédito
CREATE TABLE credit_cards (
    id_credit_card BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_number VARCHAR(20) NOT NULL UNIQUE,
    expiry_date VARCHAR(10) NOT NULL,
    cvc VARCHAR(4) NOT NULL,
    full_name VARCHAR(200) NOT NULL,
    bank_account_id BIGINT NOT NULL,
    CONSTRAINT fk_credit_card_bank_account FOREIGN KEY (bank_account_id)
        REFERENCES bank_accounts(id_bank_account) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índices para mejorar rendimiento
CREATE INDEX idx_credit_cards_account ON credit_cards(bank_account_id);
CREATE INDEX idx_credit_cards_number ON credit_cards(card_number);
