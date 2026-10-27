package com.helpflow.support.domain.model;

import com.helpflow.support.domain.enums.TicketStatus;

import java.time.LocalDateTime;

public class TicketHistory {
    private Long id;
    private final Long ticketId;
    private final TicketStatus previousStatus;
    private final TicketStatus newStatus;
    private final String changedBy;
    private final LocalDateTime changeDate;
    private final String comments;

    public TicketHistory(Long id, Long ticketId, TicketStatus previousStatus, TicketStatus newStatus,
                         String changedBy, LocalDateTime changeDate, String comments) {
        this.id = id;
        this.ticketId = ticketId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.changeDate = changeDate;
        this.comments = comments;
    }

    public static TicketHistory create(Long ticketId, TicketStatus previousStatus, TicketStatus newStatus,
                                       String changedBy, String comments) {
        return new TicketHistory(null, ticketId, previousStatus, newStatus, changedBy, LocalDateTime.now(), comments);
    }

    public void assignId(Long id) {
        if (this.id == null) {
            this.id = id;
        }
    }

    // Getters only
    public Long getId() {
        return id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public TicketStatus getPreviousStatus() {
        return previousStatus;
    }

    public TicketStatus getNewStatus() {
        return newStatus;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public String getComments() {
        return comments;
    }
}
