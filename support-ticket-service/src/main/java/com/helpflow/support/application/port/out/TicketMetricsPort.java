package com.helpflow.support.application.port.out;

import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.enums.TicketPriority;

import java.time.Duration;

public interface TicketMetricsPort {

    void recordTicketCreated(TicketCategory category, TicketPriority priority);

    void recordFirstResponseTime(Duration duration);

    void recordResolutionTime(Duration duration);

    void recordSlaBreached(TicketCategory category, TicketPriority priority);

    void recordAuthFailure();

    void recordAccessDenied();
}
