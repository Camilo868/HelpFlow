package com.helpflow.support.application.service;

import com.helpflow.support.application.port.out.TechnicianRepositoryPort;
import com.helpflow.support.application.port.out.TicketHistoryRepositoryPort;
import com.helpflow.support.application.port.out.TicketMetricsPort;
import com.helpflow.support.application.port.out.TicketRepositoryPort;
import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.enums.TicketPriority;
import com.helpflow.support.domain.enums.TicketStatus;
import com.helpflow.support.domain.enums.UserStatus;
import com.helpflow.support.domain.exception.InvalidTicketTransitionException;
import com.helpflow.support.domain.model.Technician;
import com.helpflow.support.domain.model.Ticket;
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
class TransitionTicketServiceTest {

    @Mock
    private TicketRepositoryPort ticketRepositoryPort;

    @Mock
    private TechnicianRepositoryPort technicianRepositoryPort;

    @Mock
    private TicketHistoryRepositoryPort ticketHistoryRepositoryPort;

    @Mock
    private TicketMetricsPort ticketMetricsPort;

    @InjectMocks
    private TransitionTicketService transitionTicketService;

    private Technician assignedTech;
    private Ticket assignedTicket;

    @BeforeEach
    void setUp() {
        assignedTech = new Technician(
                10L, 2L, "TEC-014", "Carlos Redes", "carlos.redes@helpflow.com",
                TicketCategory.RED, "MEDELLIN", 5, 1,
                UserStatus.ACTIVO, LocalDateTime.now(), LocalDateTime.now()
        );

        LocalDateTime now = LocalDateTime.now();
        assignedTicket = new Ticket(
                1L, 1L, null, "Fallo en switch", "Sin respuesta ping",
                TicketCategory.RED, TicketPriority.ALTA, "MEDELLIN",
                "SW-01", now, now.plusHours(8), TicketStatus.ASIGNADO,
                10L, assignedTech, "Asignado a Carlos", null, null, now
        );
    }

    @Test
    @DisplayName("Debe iniciar progreso del ticket cambiando su estado a EN_PROGRESO")
    void shouldStartTicketProgress() {
        when(ticketRepositoryPort.findById(1L)).thenReturn(Optional.of(assignedTicket));
        when(ticketRepositoryPort.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ticket result = transitionTicketService.startProgress(1L, "carlos.redes@helpflow.com", "Iniciando revision de logs");

        assertNotNull(result);
        assertEquals(TicketStatus.EN_PROGRESO, result.getStatus());
        verify(ticketRepositoryPort, times(1)).save(assignedTicket);
        verify(ticketHistoryRepositoryPort, times(1)).save(any());
    }

    @Test
    @DisplayName("Debe resolver ticket cambiando estado a RESUELTO y registrando tiempo de resolución")
    void shouldResolveTicketSuccessfully() {
        assignedTicket.startProgress("En analisis");

        when(ticketRepositoryPort.findById(1L)).thenReturn(Optional.of(assignedTicket));
        when(ticketRepositoryPort.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ticket result = transitionTicketService.resolveTicket(1L, "carlos.redes@helpflow.com", "Puerto SFP sustituido correctamente");

        assertNotNull(result);
        assertEquals(TicketStatus.RESUELTO, result.getStatus());
        assertEquals("Puerto SFP sustituido correctamente", result.getResolutionNotes());
        verify(ticketMetricsPort, times(1)).recordResolutionTime(any());
        verify(ticketHistoryRepositoryPort, times(1)).save(any());
    }

    @Test
    @DisplayName("Debe cerrar ticket, liberar cupo del técnico y registrar fecha de cierre")
    void shouldCloseTicketAndReleaseTechnicianCapacity() {
        assignedTicket.startProgress("En proceso");
        assignedTicket.resolve("Solucionado");

        when(ticketRepositoryPort.findById(1L)).thenReturn(Optional.of(assignedTicket));
        when(ticketRepositoryPort.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(technicianRepositoryPort.findById(10L)).thenReturn(Optional.of(assignedTech));

        Ticket result = transitionTicketService.closeTicket(1L, "admin@helpflow.com");

        assertNotNull(result);
        assertEquals(TicketStatus.CERRADO, result.getStatus());
        assertNotNull(result.getClosedAt());
        assertEquals(0, assignedTech.getCurrentActiveTickets(), "La carga del técnico debe liberarse a 0");
        verify(technicianRepositoryPort, times(1)).save(assignedTech);
        verify(ticketHistoryRepositoryPort, times(1)).save(any());
    }

    @Test
    @DisplayName("Debe escalar prioridad del ticket a un nivel superior")
    void shouldEscalateTicketPriority() {
        when(ticketRepositoryPort.findById(1L)).thenReturn(Optional.of(assignedTicket));
        when(ticketRepositoryPort.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ticket result = transitionTicketService.escalateTicket(1L, "admin@helpflow.com", "Impacto masivo detectado en sede");

        assertNotNull(result);
        assertEquals(TicketPriority.CRITICA, result.getPriority());
        verify(ticketHistoryRepositoryPort, times(1)).save(any());
    }

    @Test
    @DisplayName("Debe rechazar transición inválida lanzando InvalidTicketTransitionException")
    void shouldThrowExceptionOnInvalidTransition() {
        // Un ticket en ASIGNADO no puede cerrarse directamente sin ser resuelto primero
        when(ticketRepositoryPort.findById(1L)).thenReturn(Optional.of(assignedTicket));

        assertThrows(
                InvalidTicketTransitionException.class,
                () -> transitionTicketService.closeTicket(1L, "admin@helpflow.com")
        );

        verify(ticketRepositoryPort, never()).save(any());
    }
}
