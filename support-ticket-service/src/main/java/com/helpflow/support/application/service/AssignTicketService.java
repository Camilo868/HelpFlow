package com.helpflow.support.application.service;

import com.helpflow.support.application.dto.DispatchResult;
import com.helpflow.support.application.port.in.AssignTicketUseCase;
import com.helpflow.support.application.port.out.AssignmentRepositoryPort;
import com.helpflow.support.application.port.out.TechnicianDispatchPort;
import com.helpflow.support.application.port.out.TechnicianRepositoryPort;
import com.helpflow.support.application.port.out.TicketHistoryRepositoryPort;
import com.helpflow.support.application.port.out.TicketRepositoryPort;
import com.helpflow.support.domain.enums.TechnicianAvailability;
import com.helpflow.support.domain.enums.TicketStatus;
import com.helpflow.support.domain.exception.EntityNotFoundException;
import com.helpflow.support.domain.exception.TechnicianCapacityExceededException;
import com.helpflow.support.domain.model.Assignment;
import com.helpflow.support.domain.model.Technician;
import com.helpflow.support.domain.model.Ticket;
import com.helpflow.support.domain.model.TicketHistory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AssignTicketService implements AssignTicketUseCase {

    private final TicketRepositoryPort ticketRepositoryPort;
    private final TechnicianRepositoryPort technicianRepositoryPort;
    private final AssignmentRepositoryPort assignmentRepositoryPort;
    private final TicketHistoryRepositoryPort ticketHistoryRepositoryPort;
    private final TechnicianDispatchPort technicianDispatchPort;

    public AssignTicketService(TicketRepositoryPort ticketRepositoryPort,
                               TechnicianRepositoryPort technicianRepositoryPort,
                               AssignmentRepositoryPort assignmentRepositoryPort,
                               TicketHistoryRepositoryPort ticketHistoryRepositoryPort,
                               TechnicianDispatchPort technicianDispatchPort) {
        this.ticketRepositoryPort = ticketRepositoryPort;
        this.technicianRepositoryPort = technicianRepositoryPort;
        this.assignmentRepositoryPort = assignmentRepositoryPort;
        this.ticketHistoryRepositoryPort = ticketHistoryRepositoryPort;
        this.technicianDispatchPort = technicianDispatchPort;
    }

    @Override
    public Assignment assignTicket(Long ticketId, String assignedBy) {
        Ticket ticket = ticketRepositoryPort.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket", ticketId));

        TicketStatus previousStatus = ticket.getStatus();

        // 1. Consult dispatch external service
        Optional<DispatchResult> dispatchOpt = technicianDispatchPort.requestDispatch(
                ticket.getId(),
                ticket.getCategory(),
                ticket.getPriority()
        );

        TechnicianAvailability availability = dispatchOpt.map(DispatchResult::availability).orElse(TechnicianAvailability.MEDIA);
        int estimatedHours = dispatchOpt.map(DispatchResult::estimatedHours).orElse(ticket.getPriority().getDefaultSlaHours());

        // 2. Select technician respecting specialty and capacity
        Technician technician = selectTechnician(ticket, dispatchOpt);

        // 3. Apply assignment invariants
        technician.incrementActiveTickets();
        technicianRepositoryPort.save(technician);

        ticket.assignTechnician(technician, "Assigned to " + technician.getName());
        ticketRepositoryPort.save(ticket);

        // 4. Save assignment record and history in transaction
        Assignment assignment = Assignment.create(
                ticket.getId(),
                technician.getId(),
                assignedBy,
                availability,
                estimatedHours,
                "Assigned with availability " + availability + " and estimated " + estimatedHours + "h"
        );
        Assignment savedAssignment = assignmentRepositoryPort.save(assignment);

        TicketHistory history = TicketHistory.create(
                ticket.getId(),
                previousStatus,
                TicketStatus.ASIGNADO,
                assignedBy,
                "Assigned to technician " + technician.getName()
        );
        ticketHistoryRepositoryPort.save(history);

        return savedAssignment;
    }

    private Technician selectTechnician(Ticket ticket, Optional<DispatchResult> dispatchOpt) {
        if (dispatchOpt.isPresent()) {
            String code = dispatchOpt.get().technicianCode();
            Optional<Technician> techByDoc = technicianRepositoryPort.findByDocument(code);
            if (techByDoc.isPresent() && isTechnicianEligible(techByDoc.get(), ticket)) {
                return techByDoc.get();
            }
        }

        return technicianRepositoryPort.findAvailableBySpecialtyAndHeadquarters(ticket.getCategory(), ticket.getHeadquarters())
                .stream()
                .filter(t -> isTechnicianEligible(t, ticket))
                .findFirst()
                .orElseGet(() -> technicianRepositoryPort.findAll()
                        .stream()
                        .filter(t -> isTechnicianEligible(t, ticket))
                        .findFirst()
                        .orElseThrow(() -> new TechnicianCapacityExceededException(
                                "No eligible technician with available capacity found for category " + ticket.getCategory()
                        ))
                );
    }

    private boolean isTechnicianEligible(Technician tech, Ticket ticket) {
        return tech.isActive() && tech.hasAvailableCapacity() && tech.canHandleCategory(ticket.getCategory());
    }
}
