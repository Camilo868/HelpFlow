package com.helpflow.support.infrastructure.adapter.out.persistence.repository;

import com.helpflow.support.infrastructure.adapter.out.persistence.entity.AssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataJpaAssignmentRepository extends JpaRepository<AssignmentEntity, Long> {

    List<AssignmentEntity> findByTicketId(Long ticketId);

    List<AssignmentEntity> findByTechnicianId(Long technicianId);
}
