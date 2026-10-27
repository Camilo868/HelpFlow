package com.helpflow.support.infrastructure.adapter.out.persistence.adapter;

import com.helpflow.support.application.port.out.TechnicianRepositoryPort;
import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.model.Technician;
import com.helpflow.support.infrastructure.adapter.out.persistence.entity.TechnicianEntity;
import com.helpflow.support.infrastructure.adapter.out.persistence.mapper.TechnicianPersistenceMapper;
import com.helpflow.support.infrastructure.adapter.out.persistence.repository.SpringDataJpaTechnicianRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TechnicianPersistenceAdapter implements TechnicianRepositoryPort {

    private final SpringDataJpaTechnicianRepository technicianRepository;
    private final TechnicianPersistenceMapper technicianMapper;

    public TechnicianPersistenceAdapter(SpringDataJpaTechnicianRepository technicianRepository,
                                       TechnicianPersistenceMapper technicianMapper) {
        this.technicianRepository = technicianRepository;
        this.technicianMapper = technicianMapper;
    }

    @Override
    public Technician save(Technician technician) {
        TechnicianEntity entity = technicianMapper.toEntity(technician);
        TechnicianEntity saved = technicianRepository.save(entity);
        return technicianMapper.toDomain(saved);
    }

    @Override
    public Optional<Technician> findById(Long id) {
        return technicianRepository.findById(id).map(technicianMapper::toDomain);
    }

    @Override
    public Optional<Technician> findByUserId(Long userId) {
        return technicianRepository.findByUserId(userId).map(technicianMapper::toDomain);
    }

    @Override
    public Optional<Technician> findByDocument(String document) {
        return technicianRepository.findByDocument(document).map(technicianMapper::toDomain);
    }

    @Override
    public Optional<Technician> findByEmail(String email) {
        return technicianRepository.findByEmail(email).map(technicianMapper::toDomain);
    }

    @Override
    public List<Technician> findAvailableBySpecialtyAndHeadquarters(TicketCategory specialty, String headquarters) {
        return technicianRepository.findAvailableBySpecialtyAndHeadquarters(specialty, headquarters)
                .stream()
                .map(technicianMapper::toDomain)
                .toList();
    }

    @Override
    public List<Technician> findAll() {
        return technicianRepository.findAll().stream().map(technicianMapper::toDomain).toList();
    }

    @Override
    public boolean existsByDocument(String document) {
        return technicianRepository.existsByDocument(document);
    }

    @Override
    public boolean existsByEmail(String email) {
        return technicianRepository.existsByEmail(email);
    }
}
