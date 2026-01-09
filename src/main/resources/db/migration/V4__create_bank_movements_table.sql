-- Crear tabla de movimientos bancarios
CREATE TABLE bank_movements (
    id_bank_movement BIGINT AUTO_INCREMENT PRIMARY KEY,
    movement_type VARCHAR(20) NOT NULL,
    origin_movement VARCHAR(20) NOT NULL,
    credit_card_id BIGINT,
    bank_account_id BIGINT,
    movement_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    amount DECIMAL(19, 2) NOT NULL,
    concept VARCHAR(500) NOT NULL,
    CONSTRAINT fk_bank_movements_credit_card FOREIGN KEY (credit_card_id) REFERENCES credit_cards(id_credit_card) ON DELETE SET NULL,
    CONSTRAINT fk_bank_movements_bank_account FOREIGN KEY (bank_account_id) REFERENCES bank_accounts(id_bank_account) ON DELETE SET NULL,
    CONSTRAINT chk_movement_type CHECK (movement_type IN ('DEBE', 'HABER')),
    CONSTRAINT chk_origin_movement CHECK (origin_movement IN ('TRANSFERENCIA', 'DOMICILIACION', 'TARJETA'))
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_bank_movements_type ON bank_movements(movement_type);
CREATE INDEX idx_bank_movements_origin ON bank_movements(origin_movement);
CREATE INDEX idx_bank_movements_credit_card ON bank_movements(credit_card_id);
CREATE INDEX idx_bank_movements_bank_account ON bank_movements(bank_account_id);
CREATE INDEX idx_bank_movements_date ON bank_movements(movement_date);
