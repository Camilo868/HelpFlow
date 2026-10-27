package com.helpflow.support.infrastructure.adapter.in.rest.dto;

import com.helpflow.support.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Document is required")
        String document,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Corporate email is required")
        @Email(message = "Must be a valid email format")
        String corporateEmail,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @NotBlank(message = "Headquarters is required")
        String headquarters,

        @NotBlank(message = "Department is required")
        String department,

        @NotNull(message = "Role is required (ROLE_USUARIO, ROLE_TECNICO, ROLE_ADMIN)")
        Role role
) {
}
