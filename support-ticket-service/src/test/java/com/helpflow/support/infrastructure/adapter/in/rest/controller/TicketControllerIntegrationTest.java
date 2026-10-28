package com.helpflow.support.infrastructure.adapter.in.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpflow.support.application.port.out.PasswordEncoderPort;
import com.helpflow.support.application.port.out.TechnicianRepositoryPort;
import com.helpflow.support.application.port.out.UserRepositoryPort;
import com.helpflow.support.domain.enums.Role;
import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.enums.TicketPriority;
import com.helpflow.support.domain.enums.UserStatus;
import com.helpflow.support.domain.model.Technician;
import com.helpflow.support.domain.model.User;
import com.helpflow.support.infrastructure.adapter.in.rest.dto.CreateTicketRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class TicketControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private UserRepositoryPort userRepositoryPort;

    @Autowired
    private TechnicianRepositoryPort technicianRepositoryPort;

    @Autowired
    private PasswordEncoderPort passwordEncoderPort;

    private MockMvc mockMvc;
    private User testUser;
    private Technician testTechnician;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        if (userRepositoryPort.findByCorporateEmail("juan.usuario@helpflow.com").isEmpty()) {
            User user = new User(
                    null, "1000000003", "Juan Usuario", "juan.usuario@helpflow.com",
                    passwordEncoderPort.encode("Password123!"), "MEDELLIN", "FINANZAS",
                    Role.ROLE_USUARIO, UserStatus.ACTIVO, LocalDateTime.now(), LocalDateTime.now()
            );
            testUser = userRepositoryPort.save(user);
        } else {
            testUser = userRepositoryPort.findByCorporateEmail("juan.usuario@helpflow.com").get();
        }

        if (technicianRepositoryPort.findByDocument("TEC-014").isEmpty()) {
            Technician tech = new Technician(
                    null, null, "TEC-014", "Carlos Redes", "carlos.redes@helpflow.com",
                    TicketCategory.RED, "MEDELLIN", 5, 0,
                    UserStatus.ACTIVO, LocalDateTime.now(), LocalDateTime.now()
            );
            testTechnician = technicianRepositoryPort.save(tech);
        } else {
            testTechnician = technicianRepositoryPort.findByDocument("TEC-014").get();
        }
    }

    @Test
    @DisplayName("Debe rechazar solicitudes no autenticadas con 403 Forbidden")
    void shouldRejectUnauthenticatedRequests() throws Exception {
        mockMvc.perform(get("/tickets"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "juan.usuario@helpflow.com", roles = {"USUARIO"})
    @DisplayName("Usuario con ROLE_USUARIO puede crear ticket en estado ABIERTO (201 Created)")
    void shouldAllowUserToCreateTicket() throws Exception {
        CreateTicketRequest request = new CreateTicketRequest(
                testUser.getId(),
                "Impresora de finanzas bloqueada",
                "Atasco constante en bandeja 2",
                TicketCategory.HARDWARE,
                TicketPriority.MEDIA,
                "MEDELLIN",
                "IMP-FIN-02"
        );

        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Impresora de finanzas bloqueada"))
                .andExpect(jsonPath("$.category").value("HARDWARE"))
                .andExpect(jsonPath("$.status").value("ABIERTO"))
                .andExpect(jsonPath("$.slaDeadline").exists());
    }

    @Test
    @WithMockUser(username = "admin@helpflow.com", roles = {"ADMIN"})
    @DisplayName("Administrador con ROLE_ADMIN puede listar todos los tickets (200 OK)")
    void shouldAllowAdminToGetAllTickets() throws Exception {
        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "juan.usuario@helpflow.com", roles = {"USUARIO"})
    @DisplayName("Debe denegar asignación de tickets a usuarios con ROLE_USUARIO (403 Forbidden)")
    void shouldDenyTicketAssignmentToStandardUser() throws Exception {
        mockMvc.perform(post("/tickets/1/assign"))
                .andExpect(status().isForbidden());
    }
}
