package com.helpflow.support.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El correo corporativo es obligatorio")
        @Email(message = "El correo corporativo debe tener un formato de email válido")
        String corporateEmail,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
