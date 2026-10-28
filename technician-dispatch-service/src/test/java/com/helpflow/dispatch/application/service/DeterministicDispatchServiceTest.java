package com.helpflow.dispatch.application.service;

import com.helpflow.dispatch.domain.enums.AvailabilityLevel;
import com.helpflow.dispatch.domain.enums.TicketCategory;
import com.helpflow.dispatch.domain.enums.TicketPriority;
import com.helpflow.dispatch.domain.model.DispatchRecommendation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeterministicDispatchServiceTest {

    private DeterministicDispatchService dispatchService;

    @BeforeEach
    void setUp() {
        dispatchService = new DeterministicDispatchService();
    }

    @Test
    @DisplayName("Should return exactly the same dispatch recommendation for identical inputs (pure determinism)")
    void shouldReturnDeterministicRecommendationForSameInputs() {
        Long ticketId = 125L;
        TicketCategory category = TicketCategory.RED;
        TicketPriority priority = TicketPriority.ALTA;

        DispatchRecommendation firstCall = dispatchService.dispatch(ticketId, category, priority);
        DispatchRecommendation secondCall = dispatchService.dispatch(ticketId, category, priority);

        assertEquals(firstCall.getTechnicianDocument(), secondCall.getTechnicianDocument());
        assertEquals(firstCall.getEstimatedAvailabilityHours(), secondCall.getEstimatedAvailabilityHours());
        assertEquals(firstCall.getAvailabilityLevel(), secondCall.getAvailabilityLevel());
        assertEquals(firstCall.getTechnicianName(), secondCall.getTechnicianName());
        assertEquals(firstCall.getSpecialty(), secondCall.getSpecialty());
    }

    @Test
    @DisplayName("Should assign technician with matching specialty")
    void shouldAssignTechnicianWithMatchingSpecialty() {
        DispatchRecommendation recRed = dispatchService.dispatch(10L, TicketCategory.RED, TicketPriority.MEDIA);
        assertEquals("RED", recRed.getSpecialty());

        DispatchRecommendation recHw = dispatchService.dispatch(20L, TicketCategory.HARDWARE, TicketPriority.ALTA);
        assertEquals("HARDWARE", recHw.getSpecialty());

        DispatchRecommendation recSw = dispatchService.dispatch(30L, TicketCategory.SOFTWARE, TicketPriority.CRITICA);
        assertEquals("SOFTWARE", recSw.getSpecialty());
    }

    @Test
    @DisplayName("Should correctly classify availability level according to rubric rules")
    void shouldClassifyAvailabilityLevelCorrectly() {
        assertEquals(AvailabilityLevel.ALTA, AvailabilityLevel.fromEstimatedHours(1));
        assertEquals(AvailabilityLevel.ALTA, AvailabilityLevel.fromEstimatedHours(2));
        assertEquals(AvailabilityLevel.MEDIA, AvailabilityLevel.fromEstimatedHours(3));
        assertEquals(AvailabilityLevel.MEDIA, AvailabilityLevel.fromEstimatedHours(8));
        assertEquals(AvailabilityLevel.BAJA, AvailabilityLevel.fromEstimatedHours(9));
        assertEquals(AvailabilityLevel.BAJA, AvailabilityLevel.fromEstimatedHours(15));
    }
}
