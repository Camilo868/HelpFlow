package com.helpflow.support.infrastructure.adapter.in.rest.dto;

import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.enums.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTicketRequest(
        @NotNull(message = "Requester ID is required")
        Long requesterId,

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Category is required (HARDWARE, SOFTWARE, RED, ACCESOS, OTRO)")
        TicketCategory category,

        @NotNull(message = "Priority is required (BAJA, MEDIA, ALTA, CRITICA)")
        TicketPriority priority,

        @NotBlank(message = "Headquarters is required")
        String headquarters,

        @NotBlank(message = "Affected asset is required")
        String affectedAsset
) {
}
