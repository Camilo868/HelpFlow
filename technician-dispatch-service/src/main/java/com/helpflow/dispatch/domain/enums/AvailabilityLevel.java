package com.helpflow.dispatch.domain.enums;

public enum AvailabilityLevel {
    ALTA,
    MEDIA,
    BAJA;

    public static AvailabilityLevel fromEstimatedHours(int hours) {
        if (hours <= 2) {
            return ALTA;
        } else if (hours <= 8) {
            return MEDIA;
        } else {
            return BAJA;
        }
    }
}
