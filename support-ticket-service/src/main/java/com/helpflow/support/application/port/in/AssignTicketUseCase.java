package com.helpflow.support.application.port.in;

import com.helpflow.support.domain.model.Assignment;

public interface AssignTicketUseCase {

    Assignment assignTicket(Long ticketId, String assignedBy);
}
