package com.helpflow.support.infrastructure.adapter.out.persistence.repository;

import com.helpflow.support.domain.enums.TicketStatus;
import com.helpflow.support.infrastructure.adapter.out.persistence.entity.TicketEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface SpringDataJpaTicketRepository extends JpaRepository<TicketEntity, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"requester", "assignedTechnician"})
    Optional<TicketEntity> findById(@NonNull Long id);

    @NonNull
    @Override
    @EntityGraph(attributePaths = {"requester", "assignedTechnician"})
    List<TicketEntity> findAll();

    @EntityGraph(attributePaths = {"requester", "assignedTechnician"})
    List<TicketEntity> findByRequesterId(Long requesterId);

    @EntityGraph(attributePaths = {"requester", "assignedTechnician"})
    List<TicketEntity> findByAssignedTechnicianId(Long technicianId);

    @EntityGraph(attributePaths = {"requester", "assignedTechnician"})
    List<TicketEntity> findByStatus(TicketStatus status);
}
