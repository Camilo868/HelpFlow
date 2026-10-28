-- V3__datos_iniciales.sql
-- Initial seed data for users and technicians
-- Default password for all initial accounts: Password123!
-- BCrypt hash: $2a$10$wT8K4I0kG9zYJ5hN9tC7c.J0hF0tY1wG1oK1mF5jQ7zB9kH6yP3sK

INSERT INTO users (document, name, corporate_email, password, headquarters, department, role, status)
VALUES 
('1000000001', 'Admin HelpFlow', 'admin@helpflow.com', '$2a$10$6QVY5az.rJ4vKjejg6j2t.8zE/viWM.UYsDkjquijFhOH6lt/Yzve', 'MEDELLIN', 'IT_ADMIN', 'ROLE_ADMIN', 'ACTIVO'),
('TEC-014', 'Carlos Redes', 'carlos.redes@helpflow.com', '$2a$10$6QVY5az.rJ4vKjejg6j2t.8zE/viWM.UYsDkjquijFhOH6lt/Yzve', 'MEDELLIN', 'INFRAESTRUCTURA', 'ROLE_TECNICO', 'ACTIVO'),
('1000000003', 'Juan Usuario', 'juan.usuario@helpflow.com', '$2a$10$6QVY5az.rJ4vKjejg6j2t.8zE/viWM.UYsDkjquijFhOH6lt/Yzve', 'MEDELLIN', 'FINANZAS', 'ROLE_USUARIO', 'ACTIVO');

INSERT INTO technicians (user_id, document, name, email, specialty, headquarters, max_active_tickets, current_active_tickets, status)
VALUES
(2, 'TEC-014', 'Carlos Redes', 'carlos.redes@helpflow.com', 'RED', 'MEDELLIN', 5, 0, 'ACTIVO'),
(NULL, 'TEC-001', 'Laura Hardware', 'laura.hardware@helpflow.com', 'HARDWARE', 'MEDELLIN', 5, 0, 'ACTIVO'),
(NULL, 'TEC-002', 'Mateo Software', 'mateo.software@helpflow.com', 'SOFTWARE', 'BOGOTA', 5, 0, 'ACTIVO');
