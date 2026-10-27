package com.helpflow.support.application.dto;

import com.helpflow.support.domain.enums.Role;

public record RegisterUserCommand(
        String document,
        String name,
        String corporateEmail,
        String password,
        String headquarters,
        String department,
        Role role
) {
}
