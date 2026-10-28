package com.helpflow.dispatch.domain.model;

import com.helpflow.dispatch.domain.enums.AvailabilityLevel;
import com.helpflow.dispatch.domain.enums.TicketCategory;
import com.helpflow.dispatch.domain.enums.TicketPriority;

public class DispatchRecommendation {

    private final Long ticketId;
    private final String technicianDocument;
    private final String technicianName;
    private final String specialty;
    private final AvailabilityLevel availabilityLevel;
    private final int estimatedAvailabilityHours;
    private final String dispatchReason;

    public DispatchRecommendation(
            Long ticketId,
            String technicianDocument,
            String technicianName,
            String specialty,
            AvailabilityLevel availabilityLevel,
            int estimatedAvailabilityHours,
            String dispatchReason
    ) {
        this.ticketId = ticketId;
        this.technicianDocument = technicianDocument;
        this.technicianName = technicianName;
        this.specialty = specialty;
        this.availabilityLevel = availabilityLevel;
        this.estimatedAvailabilityHours = estimatedAvailabilityHours;
        this.dispatchReason = dispatchReason;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public String getTechnicianDocument() {
        return technicianDocument;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public AvailabilityLevel getAvailabilityLevel() {
        return availabilityLevel;
    }

    public int getEstimatedAvailabilityHours() {
        return estimatedAvailabilityHours;
    }

    public String getDispatchReason() {
        return dispatchReason;
    }
}
