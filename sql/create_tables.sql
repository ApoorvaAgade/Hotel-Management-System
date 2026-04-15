CREATE DATABASE IF NOT EXISTS hotel_booking_db;
USE hotel_booking_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN','CUSTOMER') NOT NULL,
    is_active BIT(1) NOT NULL DEFAULT b'1',
    created_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS hotels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    location VARCHAR(200) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    amenities VARCHAR(1000) NOT NULL,
    is_active BIT(1) NOT NULL DEFAULT b'1',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    room_number VARCHAR(50) NOT NULL,
    category ENUM('SINGLE','DOUBLE','SUITE') NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    capacity INT NOT NULL,
    amenities VARCHAR(1000) NOT NULL,
    is_active BIT(1) NOT NULL DEFAULT b'1',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_room_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status ENUM('CONFIRMED','CANCELLED') NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_booking_room FOREIGN KEY (room_id) REFERENCES rooms(id)
);
