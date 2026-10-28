package com.helpflow.support.infrastructure.metrics;

import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.enums.TicketPriority;
import com.helpflow.support.application.port.out.TicketMetricsPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TicketMetrics implements TicketMetricsPort {

    private final MeterRegistry meterRegistry;
    private final Timer firstResponseTimer;
    private final Timer resolutionTimer;
    private final Counter authFailuresCounter;
    private final Counter accessDeniedCounter;

    public TicketMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.firstResponseTimer = Timer.builder("helpflow.tickets.first_response.time")
                .description("Time taken from ticket creation to first attention / technician assignment")
                .register(meterRegistry);

        this.resolutionTimer = Timer.builder("helpflow.tickets.resolution.time")
                .description("Time taken from ticket creation to resolution")
                .register(meterRegistry);

        this.authFailuresCounter = Counter.builder("helpflow.auth.failures.total")
                .description("Total number of failed authentication attempts")
                .register(meterRegistry);

        this.accessDeniedCounter = Counter.builder("helpflow.auth.access_denied.total")
                .description("Total number of access denied events (unauthorized role)")
                .register(meterRegistry);
    }

    public void recordTicketCreated(TicketCategory category, TicketPriority priority) {
        Counter.builder("helpflow.tickets.created.total")
                .description("Total number of tickets created partitioned by category and priority")
                .tag("category", (category != null) ? category.name() : "UNKNOWN")
                .tag("priority", (priority != null) ? priority.name() : "UNKNOWN")
                .register(meterRegistry)
                .increment();
    }

    public void recordFirstResponseTime(Duration duration) {
        if (duration != null && !duration.isNegative()) {
            firstResponseTimer.record(duration);
        }
    }

    public void recordResolutionTime(Duration duration) {
        if (duration != null && !duration.isNegative()) {
            resolutionTimer.record(duration);
        }
    }

    public void recordSlaBreached(TicketCategory category, TicketPriority priority) {
        Counter.builder("helpflow.tickets.sla_breached.total")
                .description("Total number of tickets that exceeded their SLA deadline")
                .tag("category", (category != null) ? category.name() : "UNKNOWN")
                .tag("priority", (priority != null) ? priority.name() : "UNKNOWN")
                .register(meterRegistry)
                .increment();
    }

    public void recordAuthFailure() {
        authFailuresCounter.increment();
    }

    public void recordAccessDenied() {
        accessDeniedCounter.increment();
    }
}
