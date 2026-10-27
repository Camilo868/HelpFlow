package com.helpflow.support.domain.enums;

public enum TicketPriority {
    BAJA(48),
    MEDIA(24),
    ALTA(8),
    CRITICA(4);

    private final int defaultSlaHours;

    TicketPriority(int defaultSlaHours) {
        this.defaultSlaHours = defaultSlaHours;
    }

    public int getDefaultSlaHours() {
        return defaultSlaHours;
    }
}
