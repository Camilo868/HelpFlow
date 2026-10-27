package com.helpflow.support.domain.model;

import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.enums.UserStatus;
import com.helpflow.support.domain.exception.TechnicianCapacityExceededException;

import java.time.LocalDateTime;

public class Technician {
    private Long id;
    private final Long userId;
    private final String document;
    private String name;
    private String email;
    private TicketCategory specialty;
    private String headquarters;
    private int maxActiveTickets;
    private int currentActiveTickets;
    private UserStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Technician(Long id, Long userId, String document, String name, String email,
                      TicketCategory specialty, String headquarters, int maxActiveTickets,
                      int currentActiveTickets, UserStatus status,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.document = document;
        this.name = name;
        this.email = email;
        this.specialty = specialty;
        this.headquarters = headquarters;
        this.maxActiveTickets = maxActiveTickets;
        this.currentActiveTickets = currentActiveTickets;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Technician createNew(Long userId, String document, String name, String email,
                                       TicketCategory specialty, String headquarters, int maxActiveTickets) {
        LocalDateTime now = LocalDateTime.now();
        return new Technician(null, userId, document, name, email, specialty,
                headquarters, maxActiveTickets, 0, UserStatus.ACTIVO, now, now);
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVO;
    }

    public boolean hasAvailableCapacity() {
        return this.currentActiveTickets < this.maxActiveTickets;
    }

    public boolean canHandleCategory(TicketCategory category) {
        return this.specialty == null || this.specialty == category || category == TicketCategory.OTRO;
    }

    public void incrementActiveTickets() {
        if (!hasAvailableCapacity()) {
            throw new TechnicianCapacityExceededException(
                    String.format("Technician '%s' has reached maximum active tickets limit of %d", name, maxActiveTickets)
            );
        }
        this.currentActiveTickets++;
        this.updatedAt = LocalDateTime.now();
    }

    public void decrementActiveTickets() {
        if (this.currentActiveTickets > 0) {
            this.currentActiveTickets--;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void updateProfile(String name, String email, TicketCategory specialty, String headquarters, int maxActiveTickets) {
        this.name = name;
        this.email = email;
        this.specialty = specialty;
        this.headquarters = headquarters;
        this.maxActiveTickets = maxActiveTickets;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.status = UserStatus.ACTIVO;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVO;
        this.updatedAt = LocalDateTime.now();
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

    public Long getUserId() {
        return userId;
    }

    public String getDocument() {
        return document;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public TicketCategory getSpecialty() {
        return specialty;
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public int getMaxActiveTickets() {
        return maxActiveTickets;
    }

    public int getCurrentActiveTickets() {
        return currentActiveTickets;
    }

    public UserStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
