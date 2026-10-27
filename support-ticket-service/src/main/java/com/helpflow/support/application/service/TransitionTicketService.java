package com.helpflow.support.application.service;

import com.helpflow.support.application.port.in.TransitionTicketUseCase;
import com.helpflow.support.application.port.out.TechnicianRepositoryPort;
import com.helpflow.support.application.port.out.TicketHistoryRepositoryPort;
import com.helpflow.support.application.port.out.TicketRepositoryPort;
import com.helpflow.support.domain.enums.TicketPriority;
import com.helpflow.support.domain.enums.TicketStatus;
import com.helpflow.support.domain.exception.EntityNotFoundException;
import com.helpflow.support.domain.model.Ticket;
import com.helpflow.support.domain.model.TicketHistory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TransitionTicketService implements TransitionTicketUseCase {

    private final TicketRepositoryPort ticketRepositoryPort;
    private final TechnicianRepositoryPort technicianRepositoryPort;
    private final TicketHistoryRepositoryPort ticketHistoryRepositoryPort;

    public TransitionTicketService(TicketRepositoryPort ticketRepositoryPort,
                                   TechnicianRepositoryPort technicianRepositoryPort,
                                   TicketHistoryRepositoryPort ticketHistoryRepositoryPort) {
        this.ticketRepositoryPort = ticketRepositoryPort;
        this.technicianRepositoryPort = technicianRepositoryPort;
        this.ticketHistoryRepositoryPort = ticketHistoryRepositoryPort;
    }

    @Override
    public Ticket startProgress(Long ticketId, String username, String observations) {
        Ticket ticket = findTicket(ticketId);
        TicketStatus previous = ticket.getStatus();

        ticket.startProgress(observations);
        Ticket saved = ticketRepositoryPort.save(ticket);

        recordHistory(saved.getId(), previous, saved.getStatus(), username, observations);
        return saved;
    }

    @Override
    public Ticket resolveTicket(Long ticketId, String username, String resolutionNotes) {
        Ticket ticket = findTicket(ticketId);
        TicketStatus previous = ticket.getStatus();

        ticket.resolve(resolutionNotes);
        Ticket saved = ticketRepositoryPort.save(ticket);

        recordHistory(saved.getId(), previous, saved.getStatus(), username, resolutionNotes);
        return saved;
    }

    @Override
    public Ticket closeTicket(Long ticketId, String username) {
        Ticket ticket = findTicket(ticketId);
        TicketStatus previous = ticket.getStatus();

        ticket.close();
        Ticket saved = ticketRepositoryPort.save(ticket);

        releaseTechnicianCapacity(ticket);
        recordHistory(saved.getId(), previous, saved.getStatus(), username, "Ticket closed successfully");
        return saved;
    }

    @Override
    public Ticket cancelTicket(Long ticketId, String username, String reason) {
        Ticket ticket = findTicket(ticketId);
        TicketStatus previous = ticket.getStatus();

        ticket.cancel(reason);
        Ticket saved = ticketRepositoryPort.save(ticket);

        releaseTechnicianCapacity(ticket);
        recordHistory(saved.getId(), previous, saved.getStatus(), username, reason);
        return saved;
    }

    @Override
    public Ticket escalateTicket(Long ticketId, String username, String reason) {
        Ticket ticket = findTicket(ticketId);
        TicketPriority previousPriority = ticket.getPriority();

        TicketPriority nextPriority = switch (previousPriority) {
            case BAJA -> TicketPriority.MEDIA;
            case MEDIA -> TicketPriority.ALTA;
            case ALTA, CRITICA -> TicketPriority.CRITICA;
        };

        ticket.escalate(nextPriority, reason);
        Ticket saved = ticketRepositoryPort.save(ticket);

        recordHistory(saved.getId(), saved.getStatus(), saved.getStatus(), username,
                String.format("Escalated from %s to %s. Reason: %s", previousPriority, nextPriority, reason));
        return saved;
    }

    private Ticket findTicket(Long ticketId) {
        return ticketRepositoryPort.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket", ticketId));
    }

    private void releaseTechnicianCapacity(Ticket ticket) {
        if (ticket.getAssignedTechnicianId() != null) {
            technicianRepositoryPort.findById(ticket.getAssignedTechnicianId()).ifPresent(tech -> {
                tech.decrementActiveTickets();
                technicianRepositoryPort.save(tech);
            });
        }
    }

    private void recordHistory(Long ticketId, TicketStatus previous, TicketStatus current, String changedBy, String comment) {
        TicketHistory history = TicketHistory.create(ticketId, previous, current, changedBy, comment);
        ticketHistoryRepositoryPort.save(history);
    }
}
