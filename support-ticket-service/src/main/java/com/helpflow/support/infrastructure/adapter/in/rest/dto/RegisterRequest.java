package com.helpflow.support.infrastructure.adapter.in.rest.dto;

import com.helpflow.support.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "El número de documento es obligatorio")
        String document,

        @NotBlank(message = "El nombre completo es obligatorio")
        String name,

        @NotBlank(message = "El correo corporativo es obligatorio")
        @Email(message = "El correo corporativo debe tener un formato de email válido")
        String corporateEmail,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,

        @NotBlank(message = "La sede es obligatoria")
        String headquarters,

        @NotBlank(message = "El departamento o área es obligatorio")
        String department,

        @NotNull(message = "El rol es obligatorio (ROLE_USUARIO, ROLE_TECNICO, ROLE_ADMIN)")
        Role role
) {
}
