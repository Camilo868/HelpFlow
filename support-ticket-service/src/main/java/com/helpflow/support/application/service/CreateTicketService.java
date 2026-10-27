package com.helpflow.support.application.service;

import com.helpflow.support.application.dto.CreateTicketCommand;
import com.helpflow.support.application.port.in.CreateTicketUseCase;
import com.helpflow.support.application.port.out.TicketHistoryRepositoryPort;
import com.helpflow.support.application.port.out.TicketRepositoryPort;
import com.helpflow.support.application.port.out.UserRepositoryPort;
import com.helpflow.support.domain.enums.TicketStatus;
import com.helpflow.support.domain.exception.EntityNotFoundException;
import com.helpflow.support.domain.model.Ticket;
import com.helpflow.support.domain.model.TicketHistory;
import com.helpflow.support.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateTicketService implements CreateTicketUseCase {

    private final TicketRepositoryPort ticketRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final TicketHistoryRepositoryPort ticketHistoryRepositoryPort;

    public CreateTicketService(TicketRepositoryPort ticketRepositoryPort,
                               UserRepositoryPort userRepositoryPort,
                               TicketHistoryRepositoryPort ticketHistoryRepositoryPort) {
        this.ticketRepositoryPort = ticketRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.ticketHistoryRepositoryPort = ticketHistoryRepositoryPort;
    }

    @Override
    public Ticket createTicket(CreateTicketCommand command) {
        User requester = userRepositoryPort.findById(command.requesterId())
                .orElseThrow(() -> new EntityNotFoundException("User", command.requesterId()));

        requester.validateCanCreateTickets();

        Ticket ticket = Ticket.createNew(
                command.requesterId(),
                command.title(),
                command.description(),
                command.category(),
                command.priority(),
                command.headquarters(),
                command.affectedAsset()
        );

        Ticket savedTicket = ticketRepositoryPort.save(ticket);

        TicketHistory history = TicketHistory.create(
                savedTicket.getId(),
                null,
                TicketStatus.ABIERTO,
                requester.getCorporateEmail(),
                "Ticket created with SLA deadline: " + savedTicket.getSlaDeadline()
        );
        ticketHistoryRepositoryPort.save(history);

        return savedTicket;
    }
}
