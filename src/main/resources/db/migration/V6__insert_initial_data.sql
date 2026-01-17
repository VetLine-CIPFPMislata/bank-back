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

-- Cuenta 1 (bank_account_id = 1)
('DEBE', 'TARJETA', 1, 1, '2026-01-01 08:10:00', 23.50, 'Desayuno cafetería'),
('DEBE', 'TARJETA', 1, 1, '2026-01-02 12:30:00', 45.99, 'Compra supermercado'),
('DEBE', 'DOMICILIACION', NULL, 1, '2026-01-03 09:00:00', 60.00, 'Seguro hogar'),
('HABER', 'TRANSFERENCIA', NULL, 1, '2026-01-04 10:00:00', 1200.00, 'Ingreso salario'),
('DEBE', 'TARJETA', 1, 1, '2026-01-05 14:20:00', 89.99, 'Compra electrónica'),
('DEBE', 'TARJETA', 1, 1, '2026-01-06 19:45:00', 12.30, 'Taxi'),
('DEBE', 'DOMICILIACION', NULL, 1, '2026-01-07 07:30:00', 30.75, 'Gimnasio'),
('HABER', 'TRANSFERENCIA', NULL, 1, '2026-01-08 09:15:00', 50.00, 'Reembolso cliente'),
('DEBE', 'TARJETA', 1, 1, '2026-01-09 20:10:00', 19.99, 'Cine'),
('DEBE', 'TARJETA', 1, 1, '2026-01-10 11:05:00', 5.50, 'Café'),
('DEBE', 'DOMICILIACION', NULL, 1, '2026-01-11 08:00:00', 85.50, 'Recibo luz'),
('HABER', 'TRANSFERENCIA', NULL, 1, '2026-01-12 09:00:00', 200.00, 'Ingreso venta'),
('DEBE', 'TARJETA', 1, 1, '2026-01-13 18:30:00', 150.00, 'Cena restaurante'),
('DEBE', 'TARJETA', 1, 1, '2026-01-14 16:20:00', 34.20, 'Gasolinera'),
('DEBE', 'DOMICILIACION', NULL, 1, '2026-01-15 07:00:00', 12.00, 'Servicio streaming'),
('HABER', 'TRANSFERENCIA', NULL, 1, '2026-01-16 10:00:00', 75.00, 'Pago recibido'),
('DEBE', 'TARJETA', 1, 1, '2026-01-17 13:40:00', 9.99, 'App compra'),

-- Cuenta 2 (bank_account_id = 2)
('DEBE', 'TARJETA', 2, 2, '2026-01-01 09:10:00', 12.00, 'Desayuno'),
('HABER', 'TRANSFERENCIA', NULL, 2, '2026-01-02 10:20:00', 250.00, 'Ingreso freelance'),
('DEBE', 'DOMICILIACION', NULL, 2, '2026-01-03 07:30:00', 60.00, 'Seguro coche'),
('DEBE', 'TARJETA', 2, 2, '2026-01-04 13:00:00', 32.50, 'Restaurante'),
('DEBE', 'TARJETA', 2, 2, '2026-01-05 18:15:00', 49.99, 'Entradas'),
('DEBE', 'DOMICILIACION', NULL, 2, '2026-01-06 08:00:00', 15.00, 'Servicio móvil'),
('HABER', 'TRANSFERENCIA', NULL, 2, '2026-01-07 09:30:00', 100.00, 'Reintegro'),
('DEBE', 'TARJETA', 2, 2, '2026-01-08 21:00:00', 9.99, 'Suscripción'),
('DEBE', 'TARJETA', 2, 2, '2026-01-09 11:25:00', 75.00, 'Ropa'),
('DEBE', 'DOMICILIACION', NULL, 2, '2026-01-10 06:50:00', 60.00, 'Seguro hogar'),
('HABER', 'TRANSFERENCIA', NULL, 2, '2026-01-11 10:00:00', 200.00, 'Ingreso venta'),
('DEBE', 'TARJETA', 2, 2, '2026-01-12 15:30:00', 180.00, 'Compra electrónica'),
('DEBE', 'TARJETA', 2, 2, '2026-01-13 19:40:00', 49.99, 'Tienda online'),
('DEBE', 'DOMICILIACION', NULL, 2, '2026-01-14 08:10:00', 5.99, 'Servicio streaming'),
('HABER', 'TRANSFERENCIA', NULL, 2, '2026-01-15 09:45:00', 300.00, 'Devolución'),
('DEBE', 'TARJETA', 2, 2, '2026-01-16 12:20:00', 22.00, 'Supermercado'),
('DEBE', 'TARJETA', 2, 2, '2026-01-17 17:50:00', 14.00, 'Cafetería'),

-- Cuenta 3 (bank_account_id = 3)
('DEBE', 'TARJETA', 3, 3, '2026-01-01 10:00:00', 59.90, 'Compra electrónica'),
('DEBE', 'TARJETA', 3, 3, '2026-01-02 14:10:00', 20.00, 'Almuerzo'),
('HABER', 'TRANSFERENCIA', NULL, 3, '2026-01-03 09:00:00', 1200.00, 'Ingreso empresa'),
('DEBE', 'DOMICILIACION', NULL, 3, '2026-01-04 07:30:00', 25.00, 'Cuota asociacion'),
('DEBE', 'TARJETA', 3, 3, '2026-01-05 18:45:00', 75.00, 'Ropa'),
('DEBE', 'TARJETA', 3, 3, '2026-01-06 19:00:00', 9.99, 'Streaming'),
('DEBE', 'DOMICILIACION', NULL, 3, '2026-01-07 08:10:00', 45.00, 'Teléfono'),
('HABER', 'TRANSFERENCIA', NULL, 3, '2026-01-08 10:50:00', 300.00, 'Cobro servicio'),
('DEBE', 'TARJETA', 3, 3, '2026-01-09 12:25:00', 15.60, 'Tienda local'),
('DEBE', 'TARJETA', 3, 3, '2026-01-10 21:30:00', 120.00, 'Electrodomésticos'),
('DEBE', 'DOMICILIACION', NULL, 3, '2026-01-11 06:45:00', 8.30, 'Agua'),
('HABER', 'TRANSFERENCIA', NULL, 3, '2026-01-12 09:10:00', 100.00, 'Reembolso'),
('DEBE', 'TARJETA', 3, 3, '2026-01-13 17:55:00', 49.99, 'Zapatos'),
('DEBE', 'TARJETA', 3, 3, '2026-01-14 13:05:00', 14.75, 'Cafetería'),
('DEBE', 'DOMICILIACION', NULL, 3, '2026-01-15 07:20:00', 60.00, 'Seguro'),
('HABER', 'TRANSFERENCIA', NULL, 3, '2026-01-16 11:00:00', 250.00, 'Pago recibido'),
('DEBE', 'TARJETA', 3, 3, '2026-01-17 16:40:00', 32.50, 'Restaurante'),

-- Cuenta 4 (bank_account_id = 4)
('DEBE', 'TARJETA', 4, 4, '2026-01-01 08:50:00', 14.75, 'Café y snacks'),
('DEBE', 'TARJETA', 4, 4, '2026-01-02 13:40:00', 180.00, 'Compra electrónica'),
('HABER', 'TRANSFERENCIA', NULL, 4, '2026-01-03 09:15:00', 500.00, 'Ingreso venta'),
('DEBE', 'DOMICILIACION', NULL, 4, '2026-01-04 07:45:00', 60.00, 'Seguro hogar'),
('DEBE', 'TARJETA', 4, 4, '2026-01-05 19:00:00', 20.00, 'Taxi 2'),
('DEBE', 'TARJETA', 4, 4, '2026-01-06 11:30:00', 49.99, 'Mercado'),
('DEBE', 'DOMICILIACION', NULL, 4, '2026-01-07 06:55:00', 12.00, 'Streaming'),
('HABER', 'TRANSFERENCIA', NULL, 4, '2026-01-08 10:40:00', 300.00, 'Ingreso cliente'),
('DEBE', 'TARJETA', 4, 4, '2026-01-09 17:05:00', 75.00, 'Ropa compra'),
('DEBE', 'TARJETA', 4, 4, '2026-01-10 20:15:00', 9.99, 'Suscripción'),
('DEBE', 'DOMICILIACION', NULL, 4, '2026-01-11 07:25:00', 48.30, 'Agua'),
('HABER', 'TRANSFERENCIA', NULL, 4, '2026-01-12 09:55:00', 120.00, 'Reembolso'),
('DEBE', 'TARJETA', 4, 4, '2026-01-13 15:40:00', 35.00, 'Restaurante'),
('DEBE', 'TARJETA', 4, 4, '2026-01-14 12:20:00', 59.99, 'Electro'),
('DEBE', 'DOMICILIACION', NULL, 4, '2026-01-15 06:40:00', 30.00, 'Mantenimiento'),
('HABER', 'TRANSFERENCIA', NULL, 4, '2026-01-16 10:30:00', 250.00, 'Pago recibido'),
('DEBE', 'TARJETA', 4, 4, '2026-01-17 18:55:00', 19.99, 'Cine y snacks'),

-- Cuenta 5 (bank_account_id = 5)
('DEBE', 'TARJETA', 5, 5, '2026-01-01 09:30:00', 220.00, 'Compra electrodomésticos'),
('DEBE', 'TARJETA', 5, 5, '2026-01-02 18:00:00', 15.60, 'Tienda local'),
('HABER', 'TRANSFERENCIA', NULL, 5, '2026-01-03 09:20:00', 300.00, 'Ingreso venta'),
('DEBE', 'DOMICILIACION', NULL, 5, '2026-01-04 07:55:00', 48.30, 'Agua'),
('DEBE', 'TARJETA', 5, 5, '2026-01-05 20:10:00', 59.00, 'Ropa'),
('DEBE', 'TARJETA', 5, 5, '2026-01-06 13:40:00', 9.99, 'Suscripción'),
('DEBE', 'DOMICILIACION', NULL, 5, '2026-01-07 06:30:00', 60.00, 'Seguro hogar'),
('HABER', 'TRANSFERENCIA', NULL, 5, '2026-01-08 10:05:00', 120.00, 'Reembolso'),
('DEBE', 'TARJETA', 5, 5, '2026-01-09 16:20:00', 32.50, 'Restaurante'),
('DEBE', 'TARJETA', 5, 5, '2026-01-10 11:15:00', 15.00, 'Aparcamiento'),
('DEBE', 'DOMICILIACION', NULL, 5, '2026-01-11 07:05:00', 30.00, 'Mantenimiento coche'),
('HABER', 'TRANSFERENCIA', NULL, 5, '2026-01-12 09:30:00', 200.00, 'Ingreso cliente'),
('DEBE', 'TARJETA', 5, 5, '2026-01-13 19:50:00', 49.99, 'Tienda online'),
('DEBE', 'TARJETA', 5, 5, '2026-01-14 14:10:00', 15.60, 'Compra tienda'),
('DEBE', 'DOMICILIACION', NULL, 5, '2026-01-15 06:55:00', 10.00, 'Donación'),
('HABER', 'TRANSFERENCIA', NULL, 5, '2026-01-16 11:20:00', 400.00, 'Pago recibido'),
('DEBE', 'TARJETA', 5, 5, '2026-01-17 17:35:00', 75.00, 'Ropa 3'),

-- Cuenta 6 (bank_account_id = 6)
('DEBE', 'TARJETA', 6, 6, '2026-01-01 11:15:00', 9.99, 'Suscripción streaming'),
('HABER', 'TRANSFERENCIA', NULL, 6, '2026-01-02 09:45:00', 300.00, 'Devolución'),
('DEBE', 'DOMICILIACION', NULL, 6, '2026-01-03 07:05:00', 48.30, 'Agua'),
('DEBE', 'TARJETA', 6, 6, '2026-01-04 18:25:00', 14.75, 'Cafetería'),
('DEBE', 'TARJETA', 6, 6, '2026-01-05 13:50:00', 59.90, 'Compra electrónica'),
('DEBE', 'DOMICILIACION', NULL, 6, '2026-01-06 06:40:00', 60.00, 'Seguro hogar'),
('HABER', 'TRANSFERENCIA', NULL, 6, '2026-01-07 10:10:00', 150.00, 'Ingreso venta'),
('DEBE', 'TARJETA', 6, 6, '2026-01-08 20:05:00', 75.00, 'Ropa compra'),
('DEBE', 'TARJETA', 6, 6, '2026-01-09 12:35:00', 7.99, 'Pequeña compra'),
('DEBE', 'DOMICILIACION', NULL, 6, '2026-01-10 07:55:00', 30.00, 'Mantenimiento'),
('HABER', 'TRANSFERENCIA', NULL, 6, '2026-01-11 09:25:00', 200.00, 'Ingreso cliente'),
('DEBE', 'TARJETA', 6, 6, '2026-01-12 16:10:00', 49.99, 'Tienda online'),
('DEBE', 'TARJETA', 6, 6, '2026-01-13 19:30:00', 220.00, 'Electrónica'),
('DEBE', 'DOMICILIACION', NULL, 6, '2026-01-14 06:20:00', 10.00, 'Donación'),
('HABER', 'TRANSFERENCIA', NULL, 6, '2026-01-15 11:00:00', 400.00, 'Pago recibido'),
('DEBE', 'TARJETA', 6, 6, '2026-01-16 14:55:00', 32.50, 'Restaurante'),
('DEBE', 'TARJETA', 6, 6, '2026-01-17 17:15:00', 11.50, 'Kiosco');
