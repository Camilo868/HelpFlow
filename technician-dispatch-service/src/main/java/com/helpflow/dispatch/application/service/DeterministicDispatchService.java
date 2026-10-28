package com.helpflow.dispatch.application.service;

import com.helpflow.dispatch.domain.enums.AvailabilityLevel;
import com.helpflow.dispatch.domain.enums.TicketCategory;
import com.helpflow.dispatch.domain.enums.TicketPriority;
import com.helpflow.dispatch.domain.model.DispatchRecommendation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class DeterministicDispatchService {

    private record SimulatedTechnician(String document, String name, String specialty) {}

    private static final Map<TicketCategory, List<SimulatedTechnician>> TECHNICIANS_BY_CATEGORY = Map.of(
            TicketCategory.RED, List.of(
                    new SimulatedTechnician("TEC-014", "Carlos Redes", "RED"),
                    new SimulatedTechnician("TEC-015", "Andres Conectividad", "RED")
            ),
            TicketCategory.HARDWARE, List.of(
                    new SimulatedTechnician("TEC-001", "Laura Hardware", "HARDWARE"),
                    new SimulatedTechnician("TEC-007", "David Equipos", "HARDWARE")
            ),
            TicketCategory.SOFTWARE, List.of(
                    new SimulatedTechnician("TEC-002", "Mateo Software", "SOFTWARE"),
                    new SimulatedTechnician("TEC-008", "Sofia Aplicaciones", "SOFTWARE")
            ),
            TicketCategory.ACCESOS, List.of(
                    new SimulatedTechnician("TEC-003", "Diana Accesos", "ACCESOS"),
                    new SimulatedTechnician("TEC-009", "Felipe Identidades", "ACCESOS")
            ),
            TicketCategory.OTRO, List.of(
                    new SimulatedTechnician("TEC-004", "Andres Soporte", "OTRO"),
                    new SimulatedTechnician("TEC-010", "Mariana Mesa", "OTRO")
            )
    );

    public DispatchRecommendation dispatch(Long ticketId, TicketCategory category, TicketPriority priority) {
        // 1. Build deterministic numeric seed from ticketId + category + priority
        long seed = computeSeed(ticketId, category, priority);
        Random random = new Random(seed);

        // 2. Select technician matching category deterministically
        List<SimulatedTechnician> eligibleTechnicians = TECHNICIANS_BY_CATEGORY.getOrDefault(
                category,
                TECHNICIANS_BY_CATEGORY.get(TicketCategory.OTRO)
        );
        SimulatedTechnician selectedTech = eligibleTechnicians.get(random.nextInt(eligibleTechnicians.size()));

        // 3. Compute estimated response time deterministically based on priority and seed
        int estimatedHours = computeEstimatedHours(priority, random);

        // 4. Classify availability: 0-2h -> ALTA, 3-8h -> MEDIA, >8h -> BAJA
        AvailabilityLevel availability = AvailabilityLevel.fromEstimatedHours(estimatedHours);

        String reason = String.format(
                "Deterministic match for ticket #%d: specialty %s with priority %s assigned to %s (est. %d hours)",
                ticketId, category, priority, selectedTech.name(), estimatedHours
        );

        return new DispatchRecommendation(
                ticketId,
                selectedTech.document(),
                selectedTech.name(),
                selectedTech.specialty(),
                availability,
                estimatedHours,
                reason
        );
    }

    private long computeSeed(Long ticketId, TicketCategory category, TicketPriority priority) {
        long hTicket = (ticketId != null) ? ticketId : 1L;
        long hCategory = (category != null) ? category.name().hashCode() : 0;
        long hPriority = (priority != null) ? priority.name().hashCode() : 0;
        return hTicket * 1000003L + hCategory * 37L + hPriority * 19L;
    }

    private int computeEstimatedHours(TicketPriority priority, Random random) {
        TicketPriority effectivePriority = (priority != null) ? priority : TicketPriority.MEDIA;
        return switch (effectivePriority) {
            case CRITICA -> 1 + random.nextInt(2);       // 1 to 2 hours -> ALTA
            case ALTA -> 2 + random.nextInt(5);          // 2 to 6 hours -> ALTA or MEDIA
            case MEDIA -> 3 + random.nextInt(6);         // 3 to 8 hours -> MEDIA
            case BAJA -> 7 + random.nextInt(6);          // 7 to 12 hours -> MEDIA or BAJA
        };
    }
}
