-- Insertar datos iniciales de ejemplo

-- Insertar clientes de ejemplo
INSERT INTO clients (username, password, nombre, apellido1, apellido2, dni, api_token) VALUES
('jperez', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'Juan', 'Pérez', 'García', '12345678A', 'token-juan-perez-123'),
('mlopez', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'María', 'López', 'Martínez', '87654321B', 'token-maria-lopez-456'),
('agarcia', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'Antonio', 'García', 'Rodríguez', '11223344C', 'token-antonio-garcia-789');

-- Insertar cuentas bancarias de ejemplo
INSERT INTO bank_accounts (iban, saldo, client_id) VALUES
('ES9121000418450200051332', 5000.00, 1),
('ES7620770024003801234567', 12500.50, 1),
('ES1234567890123456789012', 3000.75, 2),
('ES9876543210987654321098', 8900.00, 3);

-- Insertar tarjetas de crédito de ejemplo
INSERT INTO credit_cards (card_number, expiry_date, cvc, full_name, bank_account_id) VALUES
('4532015112830366', '2027-12', '123', 'Juan Pérez García', 1),
('5425233430109903', '2026-06', '456', 'Juan Pérez García', 2),
('4916338506082832', '2028-03', '789', 'María López Martínez', 3),
('4024007134564321', '2025-09', '321', 'Antonio García Rodríguez', 4);

-- Insertar movimientos bancarios de ejemplo
INSERT INTO bank_movements (movement_type, origin_movement, credit_card_id, bank_account_id, movement_date, amount, concept) VALUES
('DEBE', 'TARJETA', 1, 1, '2026-01-05 10:30:00', 45.99, 'Compra en supermercado'),
('DEBE', 'TARJETA', 1, 1, '2026-01-06 15:45:00', 120.00, 'Compra en tienda online'),
('HABER', 'TRANSFERENCIA', NULL, 1, '2026-01-07 09:00:00', 1500.00, 'Nómina enero'),
('DEBE', 'DOMICILIACION', NULL, 1, '2026-01-07 12:00:00', 85.50, 'Recibo luz'),
('DEBE', 'TARJETA', 2, 2, '2026-01-08 18:20:00', 32.50, 'Compra en restaurante');
