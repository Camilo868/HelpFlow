package com.helpflow.support.application.port.in;

import com.helpflow.support.application.dto.CreateTicketCommand;
import com.helpflow.support.domain.model.Ticket;

public interface CreateTicketUseCase {

    Ticket createTicket(CreateTicketCommand command);
}
