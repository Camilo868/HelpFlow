package com.helpflow.support.application.service;

import com.helpflow.support.application.dto.CreateTicketCommand;
import com.helpflow.support.application.port.out.TicketHistoryRepositoryPort;
import com.helpflow.support.application.port.out.TicketMetricsPort;
import com.helpflow.support.application.port.out.TicketRepositoryPort;
import com.helpflow.support.application.port.out.UserRepositoryPort;
import com.helpflow.support.domain.enums.Role;
import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.enums.TicketPriority;
import com.helpflow.support.domain.enums.UserStatus;
import com.helpflow.support.domain.exception.EntityNotFoundException;
import com.helpflow.support.domain.exception.InactiveUserException;
import com.helpflow.support.domain.model.Ticket;
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
class CreateTicketServiceTest {

    @Mock
    private TicketRepositoryPort ticketRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private TicketHistoryRepositoryPort ticketHistoryRepositoryPort;

    @Mock
    private TicketMetricsPort ticketMetricsPort;

    @InjectMocks
    private CreateTicketService createTicketService;

    private User activeUser;
    private CreateTicketCommand validCommand;

    @BeforeEach
    void setUp() {
        activeUser = new User(
                1L, "1000000003", "Juan Usuario", "juan.usuario@helpflow.com",
                "hashed", "MEDELLIN", "FINANZAS", Role.ROLE_USUARIO,
                UserStatus.ACTIVO, LocalDateTime.now(), LocalDateTime.now()
        );

        validCommand = new CreateTicketCommand(
                1L,
                "VPN connection failure",
                "Cannot reach internal subnet",
                TicketCategory.RED,
                TicketPriority.ALTA,
                "MEDELLIN",
                "VPN-01"
        );
    }

    @Test
    @DisplayName("Should successfully create a ticket, persist history and record metrics when user is active")
    void shouldCreateTicketSuccessfully() {
        when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(activeUser));
        when(ticketRepositoryPort.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket t = invocation.getArgument(0);
            return new Ticket(
                    100L, t.getRequesterId(), activeUser, t.getTitle(), t.getDescription(),
                    t.getCategory(), t.getPriority(), t.getHeadquarters(), t.getAffectedAsset(),
                    t.getCreatedAt(), t.getSlaDeadline(), t.getStatus(), null, null,
                    null, null, null, t.getUpdatedAt()
            );
        });

        Ticket created = createTicketService.createTicket(validCommand);

        assertNotNull(created);
        assertEquals(100L, created.getId());
        assertEquals("VPN connection failure", created.getTitle());
        assertEquals(TicketCategory.RED, created.getCategory());
        assertEquals(TicketPriority.ALTA, created.getPriority());

        verify(ticketRepositoryPort, times(1)).save(any(Ticket.class));
        verify(ticketHistoryRepositoryPort, times(1)).save(any());
        verify(ticketMetricsPort, times(1)).recordTicketCreated(TicketCategory.RED, TicketPriority.ALTA);
    }

    @Test
    @DisplayName("Should throw InactiveUserException when requester account is INACTIVO")
    void shouldThrowExceptionWhenRequesterIsInactive() {
        User inactiveUser = new User(
                1L, "1000000003", "Juan Inactivo", "juan.inactivo@helpflow.com",
                "hashed", "MEDELLIN", "FINANZAS", Role.ROLE_USUARIO,
                UserStatus.INACTIVO, LocalDateTime.now(), LocalDateTime.now()
        );

        when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(inactiveUser));

        assertThrows(
                InactiveUserException.class,
                () -> createTicketService.createTicket(validCommand)
        );

        verify(ticketRepositoryPort, never()).save(any());
        verify(ticketHistoryRepositoryPort, never()).save(any());
        verify(ticketMetricsPort, never()).recordTicketCreated(any(), any());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when requester user ID does not exist")
    void shouldThrowExceptionWhenRequesterNotFound() {
        when(userRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> createTicketService.createTicket(validCommand)
        );

        verify(ticketRepositoryPort, never()).save(any());
    }
}
