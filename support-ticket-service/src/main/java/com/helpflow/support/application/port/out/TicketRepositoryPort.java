package com.helpflow.support.application.port.out;

import com.helpflow.support.domain.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepositoryPort {

    Ticket save(Ticket ticket);

    Optional<Ticket> findById(Long id);

    List<Ticket> findByRequesterId(Long requesterId);

    List<Ticket> findByAssignedTechnicianId(Long technicianId);

    List<Ticket> findAll();
}
