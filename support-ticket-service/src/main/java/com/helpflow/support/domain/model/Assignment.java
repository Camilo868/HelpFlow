package com.helpflow.support.domain.model;

import com.helpflow.support.domain.enums.TechnicianAvailability;

import java.time.LocalDateTime;

public class Assignment {
    private Long id;
    private final Long ticketId;
    private final Long technicianId;
    private final LocalDateTime assignedAt;
    private final String assignedBy;
    private final TechnicianAvailability availability;
    private final Integer estimatedHours;
    private final String reason;

    public Assignment(Long id, Long ticketId, Long technicianId, LocalDateTime assignedAt,
                      String assignedBy, TechnicianAvailability availability,
                      Integer estimatedHours, String reason) {
        this.id = id;
        this.ticketId = ticketId;
        this.technicianId = technicianId;
        this.assignedAt = assignedAt;
        this.assignedBy = assignedBy;
        this.availability = availability;
        this.estimatedHours = estimatedHours;
        this.reason = reason;
    }

    public static Assignment create(Long ticketId, Long technicianId, String assignedBy,
                                    TechnicianAvailability availability, Integer estimatedHours, String reason) {
        return new Assignment(null, ticketId, technicianId, LocalDateTime.now(), assignedBy,
                availability, estimatedHours, reason);
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

    public Long getTechnicianId() {
        return technicianId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public TechnicianAvailability getAvailability() {
        return availability;
    }

    public Integer getEstimatedHours() {
        return estimatedHours;
    }

    public String getReason() {
        return reason;
    }
}
