package com.helpflow.support.application.dto;

public record LoginCommand(
        String corporateEmail,
        String password
) {
}
