package com.helpflow.support.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record TransitionTicketRequest(
        @NotBlank(message = "Comments or observations are required for state transitions")
        String comments
) {
}
