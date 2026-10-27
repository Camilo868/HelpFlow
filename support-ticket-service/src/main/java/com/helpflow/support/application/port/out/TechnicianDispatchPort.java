package com.helpflow.support.application.port.out;

import com.helpflow.support.application.dto.DispatchResult;
import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.enums.TicketPriority;

import java.util.Optional;

public interface TechnicianDispatchPort {

    Optional<DispatchResult> requestDispatch(Long ticketId, TicketCategory category, TicketPriority priority);
}
