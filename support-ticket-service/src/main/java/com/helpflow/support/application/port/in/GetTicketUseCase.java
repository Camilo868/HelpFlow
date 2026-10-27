package com.helpflow.support.application.port.in;

import com.helpflow.support.domain.model.Ticket;

import java.util.List;

public interface GetTicketUseCase {

    Ticket getTicketById(Long ticketId);

    List<Ticket> getTicketsByRequester(Long requesterId);

    List<Ticket> getTicketsByTechnician(Long technicianId);

    List<Ticket> getAllTickets();
}
