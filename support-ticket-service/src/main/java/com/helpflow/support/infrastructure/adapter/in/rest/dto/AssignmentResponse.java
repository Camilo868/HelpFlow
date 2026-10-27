package com.helpflow.support.infrastructure.adapter.in.rest.dto;

import com.helpflow.support.domain.enums.TechnicianAvailability;

import java.time.LocalDateTime;

public record AssignmentResponse(
        Long id,
        Long ticketId,
        Long technicianId,
        LocalDateTime assignedAt,
        String assignedBy,
        TechnicianAvailability availability,
        int estimatedHours,
        String reason
) {
}
