package com.helpflow.support.application.dto;

import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.enums.TicketPriority;

public record CreateTicketCommand(
        Long requesterId,
        String title,
        String description,
        TicketCategory category,
        TicketPriority priority,
        String headquarters,
        String affectedAsset
) {
}
