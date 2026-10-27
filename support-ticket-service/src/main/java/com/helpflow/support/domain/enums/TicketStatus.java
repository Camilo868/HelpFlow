package com.helpflow.support.domain.enums;

public enum TicketStatus {
    ABIERTO,
    ASIGNADO,
    EN_PROGRESO,
    RESUELTO,
    CERRADO,
    CANCELADO;

    public boolean canTransitionTo(TicketStatus target) {
        if (target == null) {
            return false;
        }

        return switch (this) {
            case ABIERTO -> target == ASIGNADO || target == CANCELADO;
            case ASIGNADO -> target == EN_PROGRESO || target == ASIGNADO || target == CANCELADO;
            case EN_PROGRESO -> target == RESUELTO || target == ASIGNADO || target == CANCELADO;
            case RESUELTO -> target == CERRADO || target == EN_PROGRESO;
            case CERRADO, CANCELADO -> false; // Terminal states
        };
    }

    public boolean isTerminal() {
        return this == CERRADO || this == CANCELADO;
    }
}
