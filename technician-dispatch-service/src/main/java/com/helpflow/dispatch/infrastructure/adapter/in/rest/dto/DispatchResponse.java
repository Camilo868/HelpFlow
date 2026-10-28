package com.helpflow.dispatch.infrastructure.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record DispatchResponse(
        @Schema(description = "Identifier of the ticket", example = "125")
        Long ticketId,

        @Schema(description = "Assigned technician document/code", example = "TEC-014")
        String tecnicoId,

        @Schema(description = "Availability classification: ALTA (0-2h), MEDIA (3-8h), BAJA (>8h)", example = "MEDIA")
        String disponibilidad,

        @Schema(description = "Estimated attention time in hours", example = "4")
        int tiempoEstimadoHoras,

        @Schema(description = "Technician full name", example = "Carlos Redes")
        String tecnicoNombre,

        @Schema(description = "Technician specialty", example = "RED")
        String especialidad,

        @Schema(description = "Deterministic dispatch explanation reason", example = "Deterministic match for ticket #125: specialty RED with priority ALTA assigned to Carlos Redes (est. 4 hours)")
        String motivo
) {
}
