package com.helpflow.support.application.service;

import com.helpflow.support.application.port.in.GetTicketUseCase;
import com.helpflow.support.application.port.out.TicketRepositoryPort;
import com.helpflow.support.domain.exception.EntityNotFoundException;
import com.helpflow.support.domain.model.Ticket;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetTicketService implements GetTicketUseCase {

    private final TicketRepositoryPort ticketRepositoryPort;

    public GetTicketService(TicketRepositoryPort ticketRepositoryPort) {
        this.ticketRepositoryPort = ticketRepositoryPort;
    }

    @Override
    public Ticket getTicketById(Long ticketId) {
        return ticketRepositoryPort.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket", ticketId));
    }

    @Override
    public List<Ticket> getTicketsByRequester(Long requesterId) {
        return ticketRepositoryPort.findByRequesterId(requesterId);
    }

    @Override
    public List<Ticket> getTicketsByTechnician(Long technicianId) {
        return ticketRepositoryPort.findByAssignedTechnicianId(technicianId);
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepositoryPort.findAll();
    }
}
