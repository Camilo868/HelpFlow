package com.helpflow.support.application.dto;

public record AuthResult(
        String token,
        String tokenType,
        Long userId,
        String corporateEmail,
        String role
) {
}
