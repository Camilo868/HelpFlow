package com.helpflow.support.application.service;

import com.helpflow.support.application.dto.DispatchResult;
import com.helpflow.support.application.port.out.*;
import com.helpflow.support.domain.enums.TechnicianAvailability;
import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.enums.TicketPriority;
import com.helpflow.support.domain.enums.TicketStatus;
import com.helpflow.support.domain.enums.UserStatus;
import com.helpflow.support.domain.exception.EntityNotFoundException;
import com.helpflow.support.domain.exception.TechnicianCapacityExceededException;
import com.helpflow.support.domain.model.Assignment;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignTicketServiceTest {

    @Mock
    private TicketRepositoryPort ticketRepositoryPort;

    @Mock
    private TechnicianRepositoryPort technicianRepositoryPort;

    @Mock
    private AssignmentRepositoryPort assignmentRepositoryPort;

    @Mock
    private TicketHistoryRepositoryPort ticketHistoryRepositoryPort;

    @Mock
    private TechnicianDispatchPort technicianDispatchPort;

    @Mock
    private TicketMetricsPort ticketMetricsPort;

    @InjectMocks
    private AssignTicketService assignTicketService;

    private Ticket openTicket;
    private Technician availableTech;

    @BeforeEach
    void setUp() {
        openTicket = Ticket.createNew(
                1L,
                "Fallo en switch de red",
                "Switch en piso 3 no responde",
                TicketCategory.RED,
                TicketPriority.ALTA,
                "MEDELLIN",
                "SW-03"
        );

        availableTech = new Technician(
                10L, 2L, "TEC-014", "Carlos Redes", "carlos.redes@helpflow.com",
                TicketCategory.RED, "MEDELLIN", 5, 1,
                UserStatus.ACTIVO, LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Debe asignar técnico exitosamente consultando el microservicio de despacho externo")
    void shouldAssignTicketViaExternalDispatchService() {
        when(ticketRepositoryPort.findById(1L)).thenReturn(Optional.of(openTicket));
        when(technicianDispatchPort.requestDispatch(any(), eq(TicketCategory.RED), eq(TicketPriority.ALTA)))
                .thenReturn(Optional.of(new DispatchResult(1L, "TEC-014", TechnicianAvailability.ALTA, 2)));

        when(technicianRepositoryPort.findByDocument("TEC-014")).thenReturn(Optional.of(availableTech));
        when(assignmentRepositoryPort.save(any(Assignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Assignment result = assignTicketService.assignTicket(1L, "admin@helpflow.com");

        assertNotNull(result);
        assertEquals(TicketStatus.ASIGNADO, openTicket.getStatus());
        assertEquals(2, availableTech.getCurrentActiveTickets(), "La carga de tickets activos debe incrementarse en 1");
        assertEquals(10L, result.getTechnicianId());
        assertEquals(TechnicianAvailability.ALTA, result.getAvailability());
        assertEquals(2, result.getEstimatedHours());

        verify(technicianRepositoryPort, times(1)).save(availableTech);
        verify(ticketRepositoryPort, times(1)).save(openTicket);
        verify(assignmentRepositoryPort, times(1)).save(any(Assignment.class));
        verify(ticketHistoryRepositoryPort, times(1)).save(any());
        verify(ticketMetricsPort, times(1)).recordFirstResponseTime(any());
    }

    @Test
    @DisplayName("Debe aplicar fallback local cuando el servicio de despacho externo no responde")
    void shouldFallbackToLocalSelectionWhenDispatchFails() {
        when(ticketRepositoryPort.findById(1L)).thenReturn(Optional.of(openTicket));
        when(technicianDispatchPort.requestDispatch(any(), any(), any())).thenReturn(Optional.empty());

        when(technicianRepositoryPort.findAvailableBySpecialtyAndHeadquarters(TicketCategory.RED, "MEDELLIN"))
                .thenReturn(List.of(availableTech));
        when(assignmentRepositoryPort.save(any(Assignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Assignment result = assignTicketService.assignTicket(1L, "admin@helpflow.com");

        assertNotNull(result);
        assertEquals(TicketStatus.ASIGNADO, openTicket.getStatus());
        verify(technicianRepositoryPort, times(1)).save(availableTech);
    }

    @Test
    @DisplayName("Debe lanzar TechnicianCapacityExceededException cuando ningún técnico tiene capacidad disponible")
    void shouldThrowExceptionWhenNoTechnicianHasCapacity() {
        Technician overloadedTech = new Technician(
                10L, 2L, "TEC-014", "Carlos Redes", "carlos.redes@helpflow.com",
                TicketCategory.RED, "MEDELLIN", 5, 5, // 5 de 5 -> lleno
                UserStatus.ACTIVO, LocalDateTime.now(), LocalDateTime.now()
        );

        when(ticketRepositoryPort.findById(1L)).thenReturn(Optional.of(openTicket));
        when(technicianDispatchPort.requestDispatch(any(), any(), any())).thenReturn(Optional.empty());
        when(technicianRepositoryPort.findAvailableBySpecialtyAndHeadquarters(TicketCategory.RED, "MEDELLIN"))
                .thenReturn(Collections.emptyList());
        when(technicianRepositoryPort.findAll()).thenReturn(List.of(overloadedTech));

        assertThrows(
                TechnicianCapacityExceededException.class,
                () -> assignTicketService.assignTicket(1L, "admin@helpflow.com")
        );

        verify(assignmentRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException si el ticket a asignar no existe")
    void shouldThrowExceptionWhenTicketNotFound() {
        when(ticketRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> assignTicketService.assignTicket(999L, "admin@helpflow.com")
        );

        verify(technicianDispatchPort, never()).requestDispatch(any(), any(), any());
    }
}
