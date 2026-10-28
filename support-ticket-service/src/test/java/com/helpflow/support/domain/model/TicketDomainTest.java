package com.helpflow.support.domain.model;

import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.enums.TicketPriority;
import com.helpflow.support.domain.enums.TicketStatus;
import com.helpflow.support.domain.exception.InvalidTicketTransitionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TicketDomainTest {

        @ParameterizedTest(name = "Prioridad {0} debe calcular fecha límite de SLA exactamente a {1} horas")
        @CsvSource({
                        "CRITICA, 4",
                        "ALTA, 8",
                        "MEDIA, 24",
                        "BAJA, 48"
        })
        @DisplayName("Debe calcular la fecha límite de SLA según la prioridad del ticket")
        void shouldCalculateInitialSlaBasedOnPriority(TicketPriority priority, int expectedHours) {
                Ticket ticket = Ticket.createNew(
                                1L,
                                "Fallo en switch de red",
                                "Switch SW-01 inaccesible",
                                TicketCategory.RED,
                                priority,
                                "MEDELLIN",
                                "SW-01");

                assertNotNull(ticket);
                assertEquals(TicketStatus.ABIERTO, ticket.getStatus());
                assertNotNull(ticket.getCreatedAt());
                assertNotNull(ticket.getSlaDeadline());
                assertEquals(ticket.getCreatedAt().plusHours(expectedHours), ticket.getSlaDeadline());
        }

        @Test
        @DisplayName("Debe ejecutar ciclo de vida completo: ABIERTO -> ASIGNADO -> EN_PROGRESO -> RESUELTO -> CERRADO")
        void shouldTransitionThroughFullHappyLifecycle() {
                Ticket ticket = Ticket.createNew(
                                1L,
                                "Problema con VPN",
                                "No conecta al cliente VPN corporativo",
                                TicketCategory.RED,
                                TicketPriority.ALTA,
                                "MEDELLIN",
                                "VPN-CLIENT");

                Technician technician = new Technician(
                                10L, 2L, "TEC-014", "Carlos Redes", "carlos.redes@helpflow.com",
                                TicketCategory.RED, "MEDELLIN", 5, 0,
                                com.helpflow.support.domain.enums.UserStatus.ACTIVO, LocalDateTime.now(),
                                LocalDateTime.now());

                // 1. Assign
                ticket.assignTechnician(technician, "Asignado a especialista de redes");
                assertEquals(TicketStatus.ASIGNADO, ticket.getStatus());
                assertEquals(10L, ticket.getAssignedTechnicianId());

                // 2. Start progress
                ticket.startProgress("Analizando tablas de enrutamiento");
                assertEquals(TicketStatus.EN_PROGRESO, ticket.getStatus());

                // 3. Resolve
                ticket.resolve("Ruta corregida en el gateway");
                assertEquals(TicketStatus.RESUELTO, ticket.getStatus());
                assertEquals("Ruta corregida en el gateway", ticket.getResolutionNotes());

                // 4. Close
                ticket.close();
                assertEquals(TicketStatus.CERRADO, ticket.getStatus());
                assertNotNull(ticket.getClosedAt());
        }

        @Test
        @DisplayName("Debe rechazar transición inválida directa de ABIERTO a CERRADO")
        void shouldRejectInvalidDirectCloseFromAbierto() {
                Ticket ticket = Ticket.createNew(
                                1L, "Test", "Descripcion de prueba", TicketCategory.SOFTWARE,
                                TicketPriority.MEDIA, "BOGOTA", "PC-01");

                InvalidTicketTransitionException ex = assertThrows(
                                InvalidTicketTransitionException.class,
                                ticket::close);

                assertTrue(ex.getMessage().contains("No se puede transicionar el ticket del estado 'ABIERTO' al estado 'CERRADO'"));
        }

        @Test
        @DisplayName("Debe detectar incumplimiento de SLA cuando el tiempo actual supera la fecha límite")
        void shouldDetectSlaBreached() {
                LocalDateTime createdAt = LocalDateTime.now().minusHours(10);
                LocalDateTime deadline = createdAt.plusHours(8);

                Ticket ticket = new Ticket(
                                1L, 1L, null, "Servidor caido", "Servidor principal no responde",
                                TicketCategory.HARDWARE, TicketPriority.ALTA, "MEDELLIN",
                                "SRV-01", createdAt, deadline, TicketStatus.EN_PROGRESO,
                                5L, null, null, null, null, createdAt);

                assertTrue(ticket.isSlaBreached(LocalDateTime.now()),
                                "El ticket debe reportarse como vencido según SLA");
        }

        @Test
        @DisplayName("No debe marcar SLA vencido si el ticket ya se encuentra en RESUELTO")
        void shouldNotBreachSlaIfTicketIsResolved() {
                LocalDateTime createdAt = LocalDateTime.now().minusHours(10);
                LocalDateTime deadline = createdAt.plusHours(8);

                Ticket ticket = new Ticket(
                                1L, 1L, null, "Servidor caido", "Servidor de produccion",
                                TicketCategory.HARDWARE, TicketPriority.ALTA, "MEDELLIN",
                                "SRV-01", createdAt, deadline, TicketStatus.RESUELTO,
                                5L, null, null, "Fuente de poder reemplazada", null, createdAt);

                assertFalse(ticket.isSlaBreached(LocalDateTime.now()),
                                "Un ticket resuelto no debe considerarse vencido");
        }

        @Test
        @DisplayName("Debe permitir escalamiento de prioridad únicamente hacia un nivel superior")
        void shouldEscalateOnlyToHigherPriority() {
                Ticket ticket = Ticket.createNew(
                                1L, "Fallo Outlook", "Error de sincronizacion",
                                TicketCategory.SOFTWARE, TicketPriority.BAJA, "MEDELLIN", "OUTLOOK");

                ticket.escalate(TicketPriority.ALTA, "Impacto extendido a presidencia");
                assertEquals(TicketPriority.ALTA, ticket.getPriority());

                ticket.escalate(TicketPriority.MEDIA, "Intento de degradar prioridad");
                assertEquals(TicketPriority.ALTA, ticket.getPriority(), "La prioridad no debe degradarse");
        }
}
