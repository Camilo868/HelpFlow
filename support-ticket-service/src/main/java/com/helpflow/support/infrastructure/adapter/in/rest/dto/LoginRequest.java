package com.helpflow.support.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Corporate email is required")
        @Email(message = "Must be a valid email format")
        String corporateEmail,

        @NotBlank(message = "Password is required")
        String password
) {
}
