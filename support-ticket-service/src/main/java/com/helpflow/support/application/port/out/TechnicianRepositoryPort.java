package com.helpflow.support.application.port.out;

import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.model.Technician;

import java.util.List;
import java.util.Optional;

public interface TechnicianRepositoryPort {

    Technician save(Technician technician);

    Optional<Technician> findById(Long id);

    Optional<Technician> findByUserId(Long userId);

    Optional<Technician> findByDocument(String document);

    Optional<Technician> findByEmail(String email);

    List<Technician> findAvailableBySpecialtyAndHeadquarters(TicketCategory specialty, String headquarters);

    List<Technician> findAll();

    boolean existsByDocument(String document);

    boolean existsByEmail(String email);
}
