package com.helpflow.support.infrastructure.adapter.in.rest.dto;

import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.enums.TicketPriority;
import com.helpflow.support.domain.enums.TicketStatus;

import java.time.LocalDateTime;

public record TicketResponse(
        Long id,
        Long requesterId,
        String title,
        String description,
        TicketCategory category,
        TicketPriority priority,
        String headquarters,
        String affectedAsset,
        LocalDateTime createdAt,
        LocalDateTime slaDeadline,
        TicketStatus status,
        Long assignedTechnicianId,
        String observations,
        String resolutionNotes,
        LocalDateTime closedAt,
        LocalDateTime updatedAt
) {
}
