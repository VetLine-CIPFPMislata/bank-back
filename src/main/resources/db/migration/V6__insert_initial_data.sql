-- Insertar datos iniciales de ejemplo
-- Las contraseñas están encriptadas con BCrypt (contraseña original: "password123")
-- Hash generado con BCrypt workload 10
INSERT INTO clients (username, password, nombre, apellido1, apellido2, dni) VALUES
('jperez', '$2a$10$gz4VW1ZResYvbq94LLnm0uid1nNS19R/VyZpo/II.MLVyFZdK65Je', 'Juan', 'Pérez', 'García', '12345678A'),
('mlopez', '$2a$10$gz4VW1ZResYvbq94LLnm0uid1nNS19R/VyZpo/II.MLVyFZdK65Je', 'María', 'López', 'Martínez', '87654321B'),
('agarcia', '$2a$10$gz4VW1ZResYvbq94LLnm0uid1nNS19R/VyZpo/II.MLVyFZdK65Je', 'Antonio', 'García', 'Rodríguez', '11223344C');

-- Insertar cuentas bancarias de ejemplo
INSERT INTO bank_accounts (iban, saldo, client_id) VALUES
('ES9121000418450200051332', 5000.00, 1),
('ES7620770024003801234567', 12500.50, 1),
('ES1234567890123456789012', 3000.75, 2),
('ES1234567890123456789890', 3670.75, 2),
('ES9876543210987654321098', 8900.00, 3),
('ES9876543210987654321568', 800.00, 3);

-- Insertar tarjetas de crédito de ejemplo
INSERT INTO credit_cards (card_number, expiry_date, cvc, full_name, bank_account_id) VALUES
('4532015112830366', '2027-12', '123', 'Juan Pérez García', 1),
('5425233430109903', '2026-06', '456', 'Juan Pérez García', 2),
('4916338506082832', '2028-03', '789', 'María López Martínez', 3),
('4916338506000832', '2028-04', '489', 'María López Martínez', 4),
('4916234506082832', '2028-03', '189', 'Antonio Garcia Rodríguez', 5),
('4024007134564321', '2025-09', '321', 'Antonio García Rodríguez', 6);

-- Insertar movimientos bancarios de ejemplo
INSERT INTO bank_movements (movement_type, origin_movement, credit_card_id, bank_account_id, movement_date, amount, concept) VALUES
('DEBE', 'TARJETA', 1, 1, '2026-01-05 10:30:00', 45.99, 'Compra en supermercado'),
('DEBE', 'TARJETA', 1, 1, '2026-01-06 15:45:00', 120.00, 'Compra en tienda online'),
('HABER', 'TRANSFERENCIA', NULL, 1, '2026-01-07 09:00:00', 1500.00, 'Nómina enero'),
('DEBE', 'DOMICILIACION', NULL, 1, '2026-01-07 12:00:00', 85.50, 'Recibo luz'),
('DEBE', 'TARJETA', 2, 2, '2026-01-08 18:20:00', 32.50, 'Compra en restaurante');

-- Movimientos adicionales para pruebas
INSERT INTO bank_movements (movement_type, origin_movement, credit_card_id, bank_account_id, movement_date, amount, concept) VALUES
('DEBE', 'TARJETA', 3, 3, '2026-01-09 11:15:00', 59.90, 'Compra electrónica'),
('DEBE', 'TARJETA', 4, 4, '2026-01-10 13:40:00', 14.75, 'Café y snacks'),
('HABER', 'TRANSFERENCIA', NULL, 2, '2026-01-10 09:00:00', 200.00, 'Reembolso cliente'),
('DEBE', 'DOMICILIACION', NULL, 2, '2026-01-11 08:30:00', 60.00, 'Seguro hogar'),
('DEBE', 'TARJETA', 5, 5, '2026-01-11 20:05:00', 220.00, 'Compra electrodomésticos'),
('HABER', 'NÓMINA', NULL, 3, '2026-01-12 09:00:00', 1200.00, 'Nómina enero'),
('DEBE', 'TARJETA', 6, 6, '2026-01-12 14:25:00', 9.99, 'Suscripción streaming'),
('DEBE', 'TRANSFERENCIA', NULL, 4, '2026-01-13 16:00:00', 350.00, 'Pago proveedor'),
('HABER', 'TRANSFERENCIA', NULL, 4, '2026-01-14 10:20:00', 500.00, 'Ingreso venta'),
('DEBE', 'TARJETA', 3, 3, '2026-01-15 19:45:00', 75.00, 'Ropa'),
('DEBE', 'DOMICILIACION', NULL, 5, '2026-01-16 07:30:00', 48.30, 'Agua'),
('DEBE', 'TARJETA', 5, 5, '2026-01-17 21:10:00', 15.60, 'Tienda local'),
('HABER', 'TRANSFERENCIA', NULL, 6, '2026-01-18 09:50:00', 300.00, 'Devolución'),
('DEBE', 'TARJETA', 4, 4, '2026-01-19 12:00:00', 180.00, 'Compra electrónica'),
('DEBE', 'TARJETA', 2, 2, '2026-01-20 17:30:00', 49.99, 'Entradas cine');
