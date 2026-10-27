package com.helpflow.support.application.port.out;

public interface TokenProviderPort {

    String generateToken(String username, String role);

    String extractUsername(String token);

    boolean validateToken(String token);
}
