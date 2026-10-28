package com.helpflow.support.infrastructure.adapter.in.rest.dto;

import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.enums.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTicketRequest(
        @NotNull(message = "El ID del solicitante es obligatorio")
        Long requesterId,

        @NotBlank(message = "El título del ticket es obligatorio")
        String title,

        @NotBlank(message = "La descripción de la incidencia es obligatoria")
        String description,

        @NotNull(message = "La categoría es obligatoria (HARDWARE, SOFTWARE, RED, ACCESOS, OTRO)")
        TicketCategory category,

        @NotNull(message = "La prioridad es obligatoria (BAJA, MEDIA, ALTA, CRITICA)")
        TicketPriority priority,

        @NotBlank(message = "La sede es obligatoria")
        String headquarters,

        @NotBlank(message = "El activo o equipo afectado es obligatorio")
        String affectedAsset
) {
}
