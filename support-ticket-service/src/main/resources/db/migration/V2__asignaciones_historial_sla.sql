-- V2__asignaciones_historial_sla.sql
-- Assignments and ticket history tables with performance indexes

CREATE TABLE assignments (
    id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
    technician_id BIGINT NOT NULL REFERENCES technicians(id) ON DELETE RESTRICT,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR(120) NOT NULL,
    availability VARCHAR(50) NOT NULL,
    estimated_hours INT NOT NULL,
    reason TEXT
);

CREATE TABLE ticket_histories (
    id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_by VARCHAR(120) NOT NULL,
    change_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    comments TEXT
);

CREATE INDEX idx_tickets_requester ON tickets(requester_id);
CREATE INDEX idx_tickets_assigned_tech ON tickets(assigned_technician_id);
CREATE INDEX idx_tickets_status ON tickets(status);
CREATE INDEX idx_tickets_sla ON tickets(sla_deadline);
CREATE INDEX idx_assignments_ticket ON assignments(ticket_id);
CREATE INDEX idx_histories_ticket ON ticket_histories(ticket_id);
