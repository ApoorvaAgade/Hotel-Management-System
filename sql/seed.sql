USE hotel_booking_db;

-- BCrypt hash for password: password
INSERT INTO users (name, email, password, role, is_active, created_at)
VALUES
('Admin User', 'admin@hotel.com', '$2a$10$FKFEh5yGDMrWs1guKal2B.A5eniDMvADGNbJYSOtMgwKJyqkatnt2', 'ADMIN', b'1', NOW()),
('Demo Customer', 'customer@hotel.com', '$2a$10$FKFEh5yGDMrWs1guKal2B.A5eniDMvADGNbJYSOtMgwKJyqkatnt2', 'CUSTOMER', b'1', NOW());

INSERT INTO hotels (name, location, description, amenities, is_active, created_at, updated_at)
VALUES
('Grand Vista', 'Pune', 'Modern business hotel near tech parks', 'wifi,pool,gym,parking', b'1', NOW(), NOW()),
('Sea Breeze', 'Goa', 'Beachside stay with sunset views', 'wifi,breakfast,beach-access,spa', b'1', NOW(), NOW());

INSERT INTO rooms (hotel_id, room_number, category, price_per_night, capacity, amenities, is_active, created_at, updated_at)
VALUES
(1, '101', 'SINGLE', 2500.00, 1, 'wifi,ac,tv', b'1', NOW(), NOW()),
(1, '102', 'DOUBLE', 3800.00, 2, 'wifi,ac,tv,mini-bar', b'1', NOW(), NOW()),
(2, '201', 'SUITE', 7200.00, 4, 'wifi,ac,tv,ocean-view,jacuzzi', b'1', NOW(), NOW());
