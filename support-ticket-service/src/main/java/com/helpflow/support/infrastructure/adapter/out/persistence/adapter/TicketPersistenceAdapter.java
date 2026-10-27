package com.helpflow.support.infrastructure.adapter.out.persistence.adapter;

import com.helpflow.support.application.port.out.TicketRepositoryPort;
import com.helpflow.support.domain.model.Ticket;
import com.helpflow.support.infrastructure.adapter.out.persistence.entity.TechnicianEntity;
import com.helpflow.support.infrastructure.adapter.out.persistence.entity.TicketEntity;
import com.helpflow.support.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.helpflow.support.infrastructure.adapter.out.persistence.mapper.TicketPersistenceMapper;
import com.helpflow.support.infrastructure.adapter.out.persistence.repository.SpringDataJpaTechnicianRepository;
import com.helpflow.support.infrastructure.adapter.out.persistence.repository.SpringDataJpaTicketRepository;
import com.helpflow.support.infrastructure.adapter.out.persistence.repository.SpringDataJpaUserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TicketPersistenceAdapter implements TicketRepositoryPort {

    private final SpringDataJpaTicketRepository ticketRepository;
    private final SpringDataJpaUserRepository userRepository;
    private final SpringDataJpaTechnicianRepository technicianRepository;
    private final TicketPersistenceMapper ticketMapper;

    public TicketPersistenceAdapter(SpringDataJpaTicketRepository ticketRepository,
                                  SpringDataJpaUserRepository userRepository,
                                  SpringDataJpaTechnicianRepository technicianRepository,
                                  TicketPersistenceMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.technicianRepository = technicianRepository;
        this.ticketMapper = ticketMapper;
    }

    @Override
    public Ticket save(Ticket ticket) {
        TicketEntity entity = ticketMapper.toEntity(ticket);

        if (ticket.getRequesterId() != null) {
            UserEntity requester = userRepository.getReferenceById(ticket.getRequesterId());
            entity.setRequester(requester);
        }

        if (ticket.getAssignedTechnicianId() != null) {
            TechnicianEntity technician = technicianRepository.getReferenceById(ticket.getAssignedTechnicianId());
            entity.setAssignedTechnician(technician);
        }

        TicketEntity saved = ticketRepository.save(entity);
        return ticketMapper.toDomain(saved);
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id).map(ticketMapper::toDomain);
    }

    @Override
    public List<Ticket> findByRequesterId(Long requesterId) {
        return ticketRepository.findByRequesterId(requesterId)
                .stream()
                .map(ticketMapper::toDomain)
                .toList();
    }

    @Override
    public List<Ticket> findByAssignedTechnicianId(Long technicianId) {
        return ticketRepository.findByAssignedTechnicianId(technicianId)
                .stream()
                .map(ticketMapper::toDomain)
                .toList();
    }

    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll()
                .stream()
                .map(ticketMapper::toDomain)
                .toList();
    }
}
