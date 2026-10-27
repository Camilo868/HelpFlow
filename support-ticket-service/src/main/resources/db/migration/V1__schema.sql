-- V1__schema.sql
-- Base schema for HelpFlow support system

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    document VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    corporate_email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    headquarters VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE technicians (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    document VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL,
    specialty VARCHAR(50),
    headquarters VARCHAR(100) NOT NULL,
    max_active_tickets INT NOT NULL DEFAULT 5,
    current_active_tickets INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,
    requester_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    headquarters VARCHAR(100) NOT NULL,
    affected_asset VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sla_deadline TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ABIERTO',
    assigned_technician_id BIGINT REFERENCES technicians(id),
    observations TEXT,
    resolution_notes TEXT,
    closed_at TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
