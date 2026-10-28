package com.helpflow.support.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record TransitionTicketRequest(
        @NotBlank(message = "Las observaciones o comentarios son obligatorios para el cambio de estado")
        String comments
) {
}
