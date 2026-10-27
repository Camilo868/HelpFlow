package com.helpflow.support.infrastructure.adapter.out.persistence.repository;

import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.infrastructure.adapter.out.persistence.entity.TechnicianEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataJpaTechnicianRepository extends JpaRepository<TechnicianEntity, Long> {

    Optional<TechnicianEntity> findByDocument(String document);

    Optional<TechnicianEntity> findByEmail(String email);

    Optional<TechnicianEntity> findByUserId(Long userId);

    @Query("SELECT t FROM TechnicianEntity t WHERE t.status = 'ACTIVO' AND (t.specialty = :specialty OR t.specialty IS NULL) AND t.headquarters = :headquarters AND t.currentActiveTickets < t.maxActiveTickets")
    List<TechnicianEntity> findAvailableBySpecialtyAndHeadquarters(
            @Param("specialty") TicketCategory specialty,
            @Param("headquarters") String headquarters
    );

    boolean existsByDocument(String document);

    boolean existsByEmail(String email);
}
