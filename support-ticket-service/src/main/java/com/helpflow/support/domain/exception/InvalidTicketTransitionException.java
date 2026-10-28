package com.helpflow.support.domain.exception;

import com.helpflow.support.domain.enums.TicketStatus;

public class InvalidTicketTransitionException extends DomainException {
    public InvalidTicketTransitionException(TicketStatus currentStatus, TicketStatus newStatus) {
        super(String.format("No se puede transicionar el ticket del estado '%s' al estado '%s'", currentStatus, newStatus));
    }

    public InvalidTicketTransitionException(String message) {
        super(message);
    }
}
