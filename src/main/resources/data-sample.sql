-- Script SQL con datos de ejemplo para Metalix Backend
-- Ejecutar después de que las tablas sean creadas por Hibernate

-- ============================================
-- MUNICIPALITIES
-- ============================================
INSERT INTO municipalities (name, code, region, population, area, contact_email, contact_phone, is_active, created_at, updated_at)
VALUES 
('Lima Metropolitana', 'LIM-001', 'Costa', 9674755, 2672.28, 'contacto@munilima.gob.pe', '+51-1-315-1919', true, NOW(), NOW()),
('Arequipa', 'AQP-001', 'Sur', 1008290, 9682.00, 'contacto@muniarequipa.gob.pe', '+51-54-221050', true, NOW(), NOW()),
('Cusco', 'CUS-001', 'Sierra', 447588, 385.00, 'contacto@municusco.gob.pe', '+51-84-227232', true, NOW(), NOW());

-- ============================================
-- ZONES
-- ============================================
INSERT INTO zones (name, municipality_id, type, coordinates, collection_schedule, description, is_active, created_at, updated_at)
VALUES 
('Miraflores Centro', 1, 'RESIDENTIAL', '{"lat": -12.1191, "lng": -77.0285}', 'Lunes, Miércoles, Viernes', 'Zona residencial centro de Miraflores', true, NOW(), NOW()),
('San Isidro Empresarial', 1, 'COMMERCIAL', '{"lat": -12.0971, "lng": -77.0364}', 'Lunes a Sábado', 'Zona comercial de San Isidro', true, NOW(), NOW()),
('Callao Industrial', 1, 'INDUSTRIAL', '{"lat": -12.0464, "lng": -77.1181}', 'Diario', 'Zona industrial del Callao', true, NOW(), NOW()),
('Cayma Residencial', 2, 'RESIDENTIAL', '{"lat": -16.3755, "lng": -71.5495}', 'Martes, Jueves, Sábado', 'Zona residencial de Cayma', true, NOW(), NOW());

-- ============================================
-- USERS (Contraseña: password123)
-- ============================================
-- Password hash de 'password123' con BCrypt: $2a$10$xZV/6x5kQO8nFzAUvwKAa.iB7ZqJWZH5E2vMaZmPvYvJzQq7XfNjO
INSERT INTO users (email, password, first_name, last_name, role, municipality_id, phone, address, city, zip_code, is_active, rfid_card, total_points, created_at, updated_at)
VALUES 
('admin@metalix.com', '$2a$10$xZV/6x5kQO8nFzAUvwKAa.iB7ZqJWZH5E2vMaZmPvYvJzQq7XfNjO', 'Admin', 'Sistema', 'SYSTEM_ADMIN', 1, '+51-999-888-777', 'Av. Principal 123', 'Lima', '15001', true, 'ADMIN001', 0, NOW(), NOW()),
('admin.lima@metalix.com', '$2a$10$xZV/6x5kQO8nFzAUvwKAa.iB7ZqJWZH5E2vMaZmPvYvJzQq7XfNjO', 'Carlos', 'Rodríguez', 'MUNICIPALITY_ADMIN', 1, '+51-999-777-666', 'Av. Municipal 456', 'Lima', '15001', true, 'MLIMA001', 0, NOW(), NOW()),
('maria.lopez@email.com', '$2a$10$xZV/6x5kQO8nFzAUvwKAa.iB7ZqJWZH5E2vMaZmPvYvJzQq7XfNjO', 'María', 'López', 'CITIZEN', 1, '+51-999-555-444', 'Jr. Las Flores 234', 'Lima', '15001', true, 'RFID001', 150, NOW(), NOW()),
('jose.perez@email.com', '$2a$10$xZV/6x5kQO8nFzAUvwKAa.iB7ZqJWZH5E2vMaZmPvYvJzQq7XfNjO', 'José', 'Pérez', 'CITIZEN', 1, '+51-999-444-333', 'Av. Los Incas 567', 'Lima', '15001', true, 'RFID002', 320, NOW(), NOW()),
('ana.torres@email.com', '$2a$10$xZV/6x5kQO8nFzAUvwKAa.iB7ZqJWZH5E2vMaZmPvYvJzQq7XfNjO', 'Ana', 'Torres', 'CITIZEN', 2, '+51-999-333-222', 'Calle Arequipa 890', 'Arequipa', '04001', true, 'RFID003', 200, NOW(), NOW());

-- ============================================
-- RFID CARDS
-- ============================================
INSERT INTO rfid_cards (card_number, user_id, status, issued_date, expiration_date, last_used, created_at, updated_at)
VALUES 
('RFID001', 4, 'ACTIVE', '2024-01-01', '2025-12-31', NOW(), NOW(), NOW()),
('RFID002', 5, 'ACTIVE', '2024-01-01', '2025-12-31', NOW(), NOW(), NOW()),
('RFID003', NULL, 'ACTIVE', '2024-01-01', '2025-12-31', NULL, NOW(), NOW()),
('RFID004', NULL, 'ACTIVE', '2024-01-01', '2025-12-31', NULL, NOW(), NOW());

-- ============================================
-- WASTE COLLECTORS
-- ============================================
INSERT INTO waste_collectors (name, type, location, municipality_id, zone_id, capacity, current_fill, last_collection, next_scheduled_collection, status, sensor_id, created_at, updated_at)
VALUES 
('Contenedor Plástico - Miraflores 01', 'PLASTIC', 'Parque Kennedy', 1, 1, 100.0, 45.0, '2024-10-01 08:00:00', '2024-10-03 08:00:00', 'ACTIVE', 'SENSOR001', NOW(), NOW()),
('Contenedor Vidrio - Miraflores 01', 'GLASS', 'Parque Kennedy', 1, 1, 100.0, 20.0, '2024-10-01 08:00:00', '2024-10-03 08:00:00', 'ACTIVE', 'SENSOR002', NOW(), NOW()),
('Contenedor Metal - San Isidro 01', 'METAL', 'Av. Javier Prado 200', 1, 2, 80.0, 65.0, '2024-10-01 09:00:00', '2024-10-02 09:00:00', 'ACTIVE', 'SENSOR003', NOW(), NOW()),
('Contenedor Papel - San Isidro 01', 'PAPER', 'Av. Javier Prado 200', 1, 2, 80.0, 88.0, '2024-10-01 09:00:00', '2024-10-02 09:00:00', 'FULL', 'SENSOR004', NOW(), NOW()),
('Contenedor Orgánico - Callao 01', 'ORGANIC', 'Av. Colonial 1000', 1, 3, 150.0, 120.0, '2024-10-01 07:00:00', '2024-10-02 07:00:00', 'ACTIVE', 'SENSOR005', NOW(), NOW());

-- ============================================
-- SENSOR DATA
-- ============================================
INSERT INTO sensor_data (sensor_id, collector_id, fill_level, temperature, battery_level, timestamp, status, created_at, updated_at)
VALUES 
('SENSOR001', 1, 45.0, 22.5, 85, '2024-10-02 10:00:00', 'ACTIVE', NOW(), NOW()),
('SENSOR002', 2, 20.0, 23.0, 90, '2024-10-02 10:00:00', 'ACTIVE', NOW(), NOW()),
('SENSOR003', 3, 65.0, 22.8, 75, '2024-10-02 10:00:00', 'ACTIVE', NOW(), NOW()),
('SENSOR004', 4, 88.0, 24.0, 60, '2024-10-02 10:00:00', 'ACTIVE', NOW(), NOW()),
('SENSOR005', 5, 80.0, 28.5, 40, '2024-10-02 10:00:00', 'LOW_BATTERY', NOW(), NOW());

-- ============================================
-- WASTE COLLECTIONS
-- ============================================
INSERT INTO waste_collections (user_id, collector_id, weight, timestamp, points, recyclable_type, verified, verification_method, municipality_id, zone_id, created_at, updated_at)
VALUES 
(4, 1, 2.5, '2024-10-01 08:30:00', 30, 'PLASTIC', true, 'RFID', 1, 1, NOW(), NOW()),
(4, 2, 1.8, '2024-10-01 09:00:00', 20, 'GLASS', true, 'RFID', 1, 1, NOW(), NOW()),
(5, 1, 3.2, '2024-10-01 10:00:00', 38, 'PLASTIC', true, 'RFID', 1, 1, NOW(), NOW()),
(5, 3, 4.5, '2024-10-01 11:00:00', 68, 'METAL', true, 'RFID', 1, 2, NOW(), NOW()),
(4, 4, 5.0, '2024-10-02 08:00:00', 50, 'PAPER', true, 'RFID', 1, 2, NOW(), NOW()),
(5, 5, 8.0, '2024-10-02 09:00:00', 64, 'ORGANIC', true, 'RFID', 1, 3, NOW(), NOW());

-- ============================================
-- REWARDS
-- ============================================
INSERT INTO rewards (name, description, points_cost, category, availability, municipality_id, image_url, expiration_date, terms_and_conditions, is_active, created_at, updated_at)
VALUES 
('Descuento 10% en Supermercado', 'Cupón de descuento del 10% en compras mayores a S/50', 50, 'DISCOUNT', 100, 1, 'https://example.com/discount-10.jpg', '2024-12-31', 'Válido solo en Lima. No acumulable.', true, NOW(), NOW()),
('Entrada al Cine', 'Entrada gratuita para cualquier función', 100, 'VOUCHER', 50, 1, 'https://example.com/cinema.jpg', '2024-12-31', 'Válido de lunes a jueves.', true, NOW(), NOW()),
('Bolsa Reutilizable Ecológica', 'Bolsa de tela reutilizable con diseño Metalix', 75, 'PRODUCT', 200, 1, 'https://example.com/bag.jpg', '2024-12-31', 'Recojo en oficina municipal.', true, NOW(), NOW()),
('Donación a ONG Ambiental', 'Donación de 10 soles a organización ecológica', 150, 'CHARITY', 1000, 1, 'https://example.com/charity.jpg', '2024-12-31', 'La municipalidad realiza la donación directamente.', true, NOW(), NOW()),
('Descuento 20% en Restaurante', 'Cupón de descuento del 20% en restaurantes ecológicos', 120, 'DISCOUNT', 30, 1, 'https://example.com/restaurant.jpg', '2024-12-31', 'Ver lista de restaurantes participantes.', true, NOW(), NOW());

-- ============================================
-- REWARD TRANSACTIONS
-- ============================================
INSERT INTO reward_transactions (user_id, reward_id, transaction_type, points, description, timestamp, status, created_at, updated_at)
VALUES 
(4, NULL, 'EARN', 30, 'Recolección de plástico', '2024-10-01 08:30:00', 'COMPLETED', NOW(), NOW()),
(4, NULL, 'EARN', 20, 'Recolección de vidrio', '2024-10-01 09:00:00', 'COMPLETED', NOW(), NOW()),
(5, NULL, 'EARN', 38, 'Recolección de plástico', '2024-10-01 10:00:00', 'COMPLETED', NOW(), NOW()),
(5, NULL, 'EARN', 68, 'Recolección de metal', '2024-10-01 11:00:00', 'COMPLETED', NOW(), NOW()),
(5, 1, 'REDEEM', 50, 'Canjeado: Descuento 10% en Supermercado', '2024-10-01 15:00:00', 'COMPLETED', NOW(), NOW()),
(4, NULL, 'EARN', 50, 'Recolección de papel', '2024-10-02 08:00:00', 'COMPLETED', NOW(), NOW()),
(5, NULL, 'EARN', 64, 'Recolección orgánico', '2024-10-02 09:00:00', 'COMPLETED', NOW(), NOW()),
(4, 3, 'REDEEM', 75, 'Canjeado: Bolsa Reutilizable Ecológica', '2024-10-02 16:00:00', 'COMPLETED', NOW(), NOW());

-- ============================================
-- REPORTS
-- ============================================
INSERT INTO reports (title, type, generated_by, generated_at, municipality_id, date_range, summary, metrics, status, created_at, updated_at)
VALUES 
('Reporte Mensual - Septiembre 2024', 'COLLECTION', 2, '2024-10-01 00:00:00', 1, '2024-09-01 to 2024-09-30', 
 'Total de 1500kg recolectados en Septiembre. Incremento del 15% respecto al mes anterior.',
 '{"total_weight": 1500, "collections": 450, "users": 120}', 'GENERATED', NOW(), NOW()),
 
('Reporte de Reciclaje - Septiembre 2024', 'RECYCLING', 2, '2024-10-01 00:00:00', 1, '2024-09-01 to 2024-09-30',
 'El plástico representa el 40% del reciclaje total. Se observa aumento en la recolección de metales.',
 '{"plastic": 600, "glass": 300, "metal": 400, "paper": 200}', 'GENERATED', NOW(), NOW());

-- ============================================
-- METRICS
-- ============================================
INSERT INTO metrics (name, value, unit, category, municipality_id, timestamp, trend, created_at, updated_at)
VALUES 
('Total Recolecciones', 450.0, 'unidades', 'COLLECTION', 1, NOW(), 'UP', NOW(), NOW()),
('Peso Total Recolectado', 1500.0, 'kg', 'COLLECTION', 1, NOW(), 'UP', NOW(), NOW()),
('Usuarios Activos', 120.0, 'usuarios', 'USER_ENGAGEMENT', 1, NOW(), 'UP', NOW(), NOW()),
('Puntos Canjeados', 2500.0, 'puntos', 'USER_ENGAGEMENT', 1, NOW(), 'STABLE', NOW(), NOW()),
('Tasa de Reciclaje', 68.5, '%', 'ENVIRONMENTAL', 1, NOW(), 'UP', NOW(), NOW()),
('CO2 Evitado', 850.0, 'kg', 'ENVIRONMENTAL', 1, NOW(), 'UP', NOW(), NOW());

-- ============================================
-- ALERTS
-- ============================================
INSERT INTO alerts (title, message, type, severity, source, municipality_id, timestamp, is_read, read_at, is_resolved, resolved_at, action_required, created_at, updated_at)
VALUES 
('Contenedor Lleno', 'El contenedor de papel en San Isidro está al 88% de capacidad', 'COLLECTOR_FULL', 'HIGH', 'SENSOR004', 1, NOW(), false, NULL, false, NULL, true, NOW(), NOW()),
('Batería Baja en Sensor', 'El sensor SENSOR005 reporta batería al 40%', 'MAINTENANCE_REQUIRED', 'MEDIUM', 'SENSOR005', 1, NOW(), false, NULL, false, NULL, true, NOW(), NOW()),
('Nuevo Usuario Registrado', 'Se registró un nuevo ciudadano en el sistema', 'INFORMATIONAL', 'LOW', 'SYSTEM', 1, NOW(), true, NOW(), false, NULL, false, NOW(), NOW()),
('Meta Mensual Alcanzada', '¡Felicitaciones! Se alcanzó la meta de 1500kg de reciclaje', 'INFORMATIONAL', 'LOW', 'SYSTEM', 1, NOW(), false, NULL, false, NULL, false, NOW(), NOW());

-- ============================================
-- NOTAS
-- ============================================
-- Contraseña para todos los usuarios de prueba: password123
-- Para usar en login:
--   - admin@metalix.com / password123 (SYSTEM_ADMIN)
--   - admin.lima@metalix.com / password123 (MUNICIPALITY_ADMIN)
--   - collector@metalix.com / password123 (WASTE_COLLECTOR)
--   - maria.lopez@email.com / password123 (CITIZEN)
--   - jose.perez@email.com / password123 (CITIZEN)

