package com.helpflow.support.application.port.out;

import com.helpflow.support.domain.model.TicketHistory;

import java.util.List;

public interface TicketHistoryRepositoryPort {

    TicketHistory save(TicketHistory ticketHistory);

    List<TicketHistory> findByTicketId(Long ticketId);
}
