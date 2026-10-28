package com.helpflow.support.application.service;

import com.helpflow.support.application.dto.AuthResult;
import com.helpflow.support.application.dto.LoginCommand;
import com.helpflow.support.application.dto.RegisterUserCommand;
import com.helpflow.support.application.port.out.PasswordEncoderPort;
import com.helpflow.support.application.port.out.TokenProviderPort;
import com.helpflow.support.application.port.out.UserRepositoryPort;
import com.helpflow.support.domain.enums.Role;
import com.helpflow.support.domain.enums.UserStatus;
import com.helpflow.support.domain.exception.AuthenticationFailedException;
import com.helpflow.support.domain.exception.InactiveUserException;
import com.helpflow.support.domain.exception.UserAlreadyExistsException;
import com.helpflow.support.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private TokenProviderPort tokenProviderPort;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User(
                1L, "1000000001", "Admin HelpFlow", "admin@helpflow.com",
                "$2a$10$hashedPassword", "MEDELLIN", "IT_ADMIN",
                Role.ROLE_ADMIN, UserStatus.ACTIVO, LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Debe registrar un nuevo usuario encriptando su contraseña con BCrypt")
    void shouldRegisterUserSuccessfully() {
        RegisterUserCommand cmd = new RegisterUserCommand(
                "1098765432", "Nuevo Usuario", "nuevo@helpflow.com",
                "Password123!", "MEDELLIN", "VENTAS", Role.ROLE_USUARIO
        );

        when(userRepositoryPort.existsByDocument("1098765432")).thenReturn(false);
        when(userRepositoryPort.existsByCorporateEmail("nuevo@helpflow.com")).thenReturn(false);
        when(passwordEncoderPort.encode("Password123!")).thenReturn("$2a$10$newHashedPassword");
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = authService.register(cmd);

        assertNotNull(created);
        assertEquals("nuevo@helpflow.com", created.getCorporateEmail());
        assertEquals("$2a$10$newHashedPassword", created.getPassword());
        assertEquals(Role.ROLE_USUARIO, created.getRole());
        assertEquals(UserStatus.ACTIVO, created.getStatus());

        verify(passwordEncoderPort, times(1)).encode("Password123!");
        verify(userRepositoryPort, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe rechazar registro con correo corporativo ya existente")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterUserCommand cmd = new RegisterUserCommand(
                "1098765432", "Nuevo Usuario", "admin@helpflow.com",
                "Password123!", "MEDELLIN", "VENTAS", Role.ROLE_USUARIO
        );

        when(userRepositoryPort.existsByDocument("1098765432")).thenReturn(false);
        when(userRepositoryPort.existsByCorporateEmail("admin@helpflow.com")).thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(cmd)
        );

        assertTrue(ex.getMessage().contains("Ya existe un usuario"));
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debe autenticar credenciales válidas y retornar token JWT Bearer")
    void shouldLoginSuccessfullyAndReturnJwt() {
        LoginCommand cmd = new LoginCommand("admin@helpflow.com", "Password123!");

        when(userRepositoryPort.findByCorporateEmail("admin@helpflow.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoderPort.matches("Password123!", "$2a$10$hashedPassword")).thenReturn(true);
        when(tokenProviderPort.generateToken("admin@helpflow.com", "ROLE_ADMIN")).thenReturn("mocked.jwt.token");

        AuthResult result = authService.login(cmd);

        assertNotNull(result);
        assertEquals("mocked.jwt.token", result.token());
        assertEquals("Bearer", result.tokenType());
        assertEquals("admin@helpflow.com", result.corporateEmail());
        assertEquals("ROLE_ADMIN", result.role());
    }

    @Test
    @DisplayName("Debe lanzar AuthenticationFailedException cuando la contraseña es incorrecta")
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        LoginCommand cmd = new LoginCommand("admin@helpflow.com", "WrongPassword");

        when(userRepositoryPort.findByCorporateEmail("admin@helpflow.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoderPort.matches("WrongPassword", "$2a$10$hashedPassword")).thenReturn(false);

        assertThrows(
                AuthenticationFailedException.class,
                () -> authService.login(cmd)
        );

        verify(tokenProviderPort, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("Debe lanzar InactiveUserException cuando el usuario está en estado INACTIVO")
    void shouldThrowExceptionWhenUserIsInactiveOnLogin() {
        User inactive = new User(
                2L, "1000000002", "Usuario Inactivo", "inactivo@helpflow.com",
                "$2a$10$hashed", "MEDELLIN", "RRHH",
                Role.ROLE_USUARIO, UserStatus.INACTIVO, LocalDateTime.now(), LocalDateTime.now()
        );

        LoginCommand cmd = new LoginCommand("inactivo@helpflow.com", "Password123!");

        when(userRepositoryPort.findByCorporateEmail("inactivo@helpflow.com")).thenReturn(Optional.of(inactive));

        assertThrows(
                InactiveUserException.class,
                () -> authService.login(cmd)
        );

        verify(tokenProviderPort, never()).generateToken(any(), any());
    }
}
