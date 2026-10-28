package com.helpflow.support.infrastructure.adapter.in.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpflow.support.TestcontainersConfiguration;
import com.helpflow.support.application.port.out.PasswordEncoderPort;
import com.helpflow.support.application.port.out.UserRepositoryPort;
import com.helpflow.support.domain.enums.Role;
import com.helpflow.support.domain.enums.UserStatus;
import com.helpflow.support.domain.model.User;
import com.helpflow.support.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.helpflow.support.infrastructure.adapter.in.rest.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepositoryPort userRepositoryPort;

    @Autowired
    private PasswordEncoderPort passwordEncoderPort;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        if (userRepositoryPort.findByCorporateEmail("admin@helpflow.com").isEmpty()) {
            User admin = new User(
                    null,
                    "1000000001",
                    "Admin HelpFlow",
                    "admin@helpflow.com",
                    passwordEncoderPort.encode("Password123!"),
                    "BOGOTA",
                    "IT",
                    Role.ROLE_ADMIN,
                    UserStatus.ACTIVO,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            userRepositoryPort.save(admin);
        }
    }

    @Test
    @DisplayName("Debe registrar un nuevo usuario exitosamente (201 Created)")
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "9876543210",
                "Usuario Integracion",
                "usuario.test" + System.currentTimeMillis() + "@helpflow.com",
                "Password123!",
                "MEDELLIN",
                "INGENIERIA",
                Role.ROLE_USUARIO
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Usuario Integracion"))
                .andExpect(jsonPath("$.role").value("ROLE_USUARIO"))
                .andExpect(jsonPath("$.status").value("ACTIVO"));
    }

    @Test
    @DisplayName("Debe autenticar credenciales y retornar token JWT Bearer (200 OK)")
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("admin@helpflow.com", "Password123!");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.corporateEmail").value("admin@helpflow.com"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Debe retornar 401 Unauthorized y ProblemDetail cuando la contraseña es incorrecta")
    void shouldReturn401OnBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest("admin@helpflow.com", "WrongPassword999!");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Authentication Failed"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.traceId").exists());
    }

    @Test
    @DisplayName("Debe retornar 400 Bad Request cuando el formato de email es inválido")
    void shouldReturn400OnInvalidEmail() throws Exception {
        LoginRequest request = new LoginRequest("invalid-email-format", "Password123!");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(400));
    }
}
