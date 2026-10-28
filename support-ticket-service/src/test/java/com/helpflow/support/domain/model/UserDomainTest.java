package com.helpflow.support.domain.model;

import com.helpflow.support.domain.enums.Role;
import com.helpflow.support.domain.enums.UserStatus;
import com.helpflow.support.domain.exception.InactiveUserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserDomainTest {

    @Test
    @DisplayName("Usuario activo puede registrar tickets sin restricciones")
    void activeUserCanCreateTickets() {
        User user = new User(
                1L, "1000000003", "Juan Usuario", "juan.usuario@helpflow.com",
                "hashedPass", "MEDELLIN", "FINANZAS", Role.ROLE_USUARIO,
                UserStatus.ACTIVO, LocalDateTime.now(), LocalDateTime.now()
        );

        assertTrue(user.isActive());
        assertDoesNotThrow(user::validateCanCreateTickets);
    }

    @Test
    @DisplayName("Usuario inactivo lanza InactiveUserException al intentar crear tickets")
    void inactiveUserThrowsException() {
        User user = new User(
                1L, "1000000003", "Juan Inactivo", "juan.inactivo@helpflow.com",
                "hashedPass", "MEDELLIN", "FINANZAS", Role.ROLE_USUARIO,
                UserStatus.INACTIVO, LocalDateTime.now(), LocalDateTime.now()
        );

        assertFalse(user.isActive());
        InactiveUserException ex = assertThrows(
                InactiveUserException.class,
                user::validateCanCreateTickets
        );

        assertTrue(ex.getMessage().contains("INACTIVO"));
    }
}
