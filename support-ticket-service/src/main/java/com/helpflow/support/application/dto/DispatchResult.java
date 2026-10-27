package com.helpflow.support.application.dto;

import com.helpflow.support.domain.enums.TechnicianAvailability;

public record DispatchResult(
        Long ticketId,
        String technicianCode,
        TechnicianAvailability availability,
        int estimatedHours
) {
}
