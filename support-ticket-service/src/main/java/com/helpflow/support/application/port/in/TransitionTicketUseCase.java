package com.helpflow.support.application.port.in;

import com.helpflow.support.domain.model.Ticket;

public interface TransitionTicketUseCase {

    Ticket startProgress(Long ticketId, String username, String observations);

    Ticket resolveTicket(Long ticketId, String username, String resolutionNotes);

    Ticket closeTicket(Long ticketId, String username);

    Ticket cancelTicket(Long ticketId, String username, String reason);

    Ticket escalateTicket(Long ticketId, String username, String reason);
}
