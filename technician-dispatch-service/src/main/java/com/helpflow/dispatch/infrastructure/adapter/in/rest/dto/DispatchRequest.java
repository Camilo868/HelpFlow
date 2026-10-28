package com.helpflow.dispatch.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.helpflow.dispatch.domain.enums.TicketCategory;
import com.helpflow.dispatch.domain.enums.TicketPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DispatchRequest(
        @Schema(description = "Identifier of the support ticket", example = "125")
        @NotNull(message = "ticketId is required")
        @Positive(message = "ticketId must be positive")
        Long ticketId,

        @Schema(description = "Category of the ticket", example = "RED")
        @NotNull(message = "categoria is required (HARDWARE, SOFTWARE, RED, ACCESOS, OTRO)")
        @JsonProperty("categoria")
        @JsonAlias({"category"})
        TicketCategory categoria,

        @Schema(description = "Priority of the ticket", example = "ALTA")
        @NotNull(message = "prioridad is required (BAJA, MEDIA, ALTA, CRITICA)")
        @JsonProperty("prioridad")
        @JsonAlias({"priority"})
        TicketPriority prioridad
) {
}
