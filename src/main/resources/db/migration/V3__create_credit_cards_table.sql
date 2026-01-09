-- Crear tabla de tarjetas de crédito
CREATE TABLE credit_cards (
    id_credit_card BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_number VARCHAR(19) NOT NULL UNIQUE,
    expiry_date VARCHAR(7) NOT NULL,
    cvc VARCHAR(4) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    bank_account_id BIGINT,
    CONSTRAINT fk_credit_cards_bank_account FOREIGN KEY (bank_account_id) REFERENCES bank_accounts(id_bank_account) ON DELETE CASCADE
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_credit_cards_numero ON credit_cards(card_number);
CREATE INDEX idx_credit_cards_bank_account ON credit_cards(bank_account_id);
