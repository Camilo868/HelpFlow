package com.helpflow.support.domain.model;

import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.enums.UserStatus;
import com.helpflow.support.domain.exception.TechnicianCapacityExceededException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TechnicianDomainTest {

    @Test
    @DisplayName("Debe gestionar cupo máximo de tickets y lanzar excepción al exceder la capacidad")
    void shouldEnforceMaxActiveTicketCapacity() {
        Technician tech = new Technician(
                1L, 2L, "TEC-014", "Carlos Redes", "carlos.redes@helpflow.com",
                TicketCategory.RED, "MEDELLIN", 3, 0,
                UserStatus.ACTIVO, LocalDateTime.now(), LocalDateTime.now()
        );

        assertTrue(tech.hasAvailableCapacity());

        tech.incrementActiveTickets(); // 1
        tech.incrementActiveTickets(); // 2
        tech.incrementActiveTickets(); // 3 (máxima capacidad alcanzada)

        assertFalse(tech.hasAvailableCapacity());
        assertEquals(3, tech.getCurrentActiveTickets());

        // Intentar asignar un 4to ticket debe lanzar TechnicianCapacityExceededException
        TechnicianCapacityExceededException ex = assertThrows(
                TechnicianCapacityExceededException.class,
                tech::incrementActiveTickets
        );

        assertTrue(ex.getMessage().contains("límite máximo"));

        // Liberar cupo
        tech.decrementActiveTickets();
        assertEquals(2, tech.getCurrentActiveTickets());
        assertTrue(tech.hasAvailableCapacity());
    }

    @Test
    @DisplayName("Debe validar coincidencia de especialidad y admitir categoría general OTRO")
    void shouldMatchSpecialty() {
        Technician netTech = new Technician(
                1L, 2L, "TEC-014", "Carlos Redes", "carlos.redes@helpflow.com",
                TicketCategory.RED, "MEDELLIN", 5, 0,
                UserStatus.ACTIVO, LocalDateTime.now(), LocalDateTime.now()
        );

        assertTrue(netTech.canHandleCategory(TicketCategory.RED));
        assertTrue(netTech.canHandleCategory(TicketCategory.OTRO));
        assertFalse(netTech.canHandleCategory(TicketCategory.HARDWARE));
        assertFalse(netTech.canHandleCategory(TicketCategory.SOFTWARE));
    }
}
