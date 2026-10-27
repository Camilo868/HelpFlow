package com.helpflow.support.domain.exception;

import com.helpflow.support.domain.enums.TicketStatus;

public class InvalidTicketTransitionException extends DomainException {
    public InvalidTicketTransitionException(TicketStatus currentStatus, TicketStatus newStatus) {
        super(String.format("Cannot transition ticket from status '%s' to '%s'", currentStatus, newStatus));
    }

    public InvalidTicketTransitionException(String message) {
        super(message);
    }
}
