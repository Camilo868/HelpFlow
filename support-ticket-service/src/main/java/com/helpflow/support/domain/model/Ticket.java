package com.helpflow.support.domain.model;

import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.enums.TicketPriority;
import com.helpflow.support.domain.enums.TicketStatus;
import com.helpflow.support.domain.exception.InvalidTicketTransitionException;

import java.time.LocalDateTime;

public class Ticket {
    private Long id;
    private final Long requesterId;
    private User requester;
    private final String title;
    private final String description;
    private final TicketCategory category;
    private TicketPriority priority;
    private final String headquarters;
    private final String affectedAsset;
    private final LocalDateTime createdAt;
    private LocalDateTime slaDeadline;
    private TicketStatus status;
    private Long assignedTechnicianId;
    private Technician assignedTechnician;
    private String observations;
    private String resolutionNotes;
    private LocalDateTime closedAt;
    private LocalDateTime updatedAt;

    // Full constructor for rebuilding from persistence
    public Ticket(Long id, Long requesterId, User requester, String title, String description,
                  TicketCategory category, TicketPriority priority, String headquarters,
                  String affectedAsset, LocalDateTime createdAt, LocalDateTime slaDeadline,
                  TicketStatus status, Long assignedTechnicianId, Technician assignedTechnician,
                  String observations, String resolutionNotes, LocalDateTime closedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.requesterId = requesterId;
        this.requester = requester;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.headquarters = headquarters;
        this.affectedAsset = affectedAsset;
        this.createdAt = createdAt;
        this.slaDeadline = slaDeadline;
        this.status = status;
        this.assignedTechnicianId = assignedTechnicianId;
        this.assignedTechnician = assignedTechnician;
        this.observations = observations;
        this.resolutionNotes = resolutionNotes;
        this.closedAt = closedAt;
        this.updatedAt = updatedAt;
    }

    // Factory method for creating a brand new Ticket in state ABIERTO with initial SLA
    public static Ticket createNew(Long requesterId, String title, String description,
                                   TicketCategory category, TicketPriority priority,
                                   String headquarters, String affectedAsset) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.plusHours(priority.getDefaultSlaHours());
        return new Ticket(null, requesterId, null, title, description, category,
                priority, headquarters, affectedAsset, now, deadline, TicketStatus.ABIERTO,
                null, null, null, null, null, now);
    }

    public void assignTechnician(Technician technician, String observations) {
        validateTransition(TicketStatus.ASIGNADO);
        this.status = TicketStatus.ASIGNADO;
        if (technician != null) {
            this.assignedTechnicianId = technician.getId();
            this.assignedTechnician = technician;
        }
        if (observations != null && !observations.isBlank()) {
            this.observations = observations;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void startProgress(String observations) {
        validateTransition(TicketStatus.EN_PROGRESO);
        this.status = TicketStatus.EN_PROGRESO;
        if (observations != null && !observations.isBlank()) {
            this.observations = observations;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void resolve(String resolutionNotes) {
        validateTransition(TicketStatus.RESUELTO);
        this.status = TicketStatus.RESUELTO;
        this.resolutionNotes = resolutionNotes;
        this.updatedAt = LocalDateTime.now();
    }

    public void close() {
        validateTransition(TicketStatus.CERRADO);
        this.status = TicketStatus.CERRADO;
        this.closedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void reopen(String reason) {
        validateTransition(TicketStatus.EN_PROGRESO);
        this.status = TicketStatus.EN_PROGRESO;
        this.observations = reason;
        this.closedAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        validateTransition(TicketStatus.CANCELADO);
        this.status = TicketStatus.CANCELADO;
        this.observations = reason;
        this.closedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void escalate(TicketPriority newPriority, String reason) {
        if (newPriority != null && newPriority.ordinal() > this.priority.ordinal()) {
            this.priority = newPriority;
            this.observations = reason;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean isSlaBreached(LocalDateTime referenceTime) {
        if (this.slaDeadline == null) {
            return false;
        }
        if (this.status == TicketStatus.RESUELTO || this.status == TicketStatus.CERRADO || this.status == TicketStatus.CANCELADO) {
            return false;
        }
        LocalDateTime now = (referenceTime != null) ? referenceTime : LocalDateTime.now();
        return now.isAfter(this.slaDeadline);
    }

    private void validateTransition(TicketStatus target) {
        if (!this.status.canTransitionTo(target)) {
            throw new InvalidTicketTransitionException(this.status, target);
        }
    }

    public void assignId(Long id) {
        if (this.id == null) {
            this.id = id;
        }
    }

    public void linkRequester(User requester) {
        this.requester = requester;
    }

    // Getters only
    public Long getId() {
        return id;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public User getRequester() {
        return requester;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public String getAffectedAsset() {
        return affectedAsset;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getSlaDeadline() {
        return slaDeadline;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public Long getAssignedTechnicianId() {
        return assignedTechnicianId;
    }

    public Technician getAssignedTechnician() {
        return assignedTechnician;
    }

    public String getObservations() {
        return observations;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
