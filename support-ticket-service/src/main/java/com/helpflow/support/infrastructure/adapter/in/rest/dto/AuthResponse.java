package com.helpflow.support.infrastructure.adapter.in.rest.dto;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String corporateEmail,
        String role
) {
}
