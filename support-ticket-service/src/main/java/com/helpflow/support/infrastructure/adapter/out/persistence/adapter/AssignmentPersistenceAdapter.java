package com.helpflow.support.infrastructure.adapter.out.persistence.adapter;

import com.helpflow.support.application.port.out.AssignmentRepositoryPort;
import com.helpflow.support.domain.model.Assignment;
import com.helpflow.support.infrastructure.adapter.out.persistence.entity.AssignmentEntity;
import com.helpflow.support.infrastructure.adapter.out.persistence.mapper.AssignmentPersistenceMapper;
import com.helpflow.support.infrastructure.adapter.out.persistence.repository.SpringDataJpaAssignmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssignmentPersistenceAdapter implements AssignmentRepositoryPort {

    private final SpringDataJpaAssignmentRepository assignmentRepository;
    private final AssignmentPersistenceMapper assignmentMapper;

    public AssignmentPersistenceAdapter(SpringDataJpaAssignmentRepository assignmentRepository,
                                       AssignmentPersistenceMapper assignmentMapper) {
        this.assignmentRepository = assignmentRepository;
        this.assignmentMapper = assignmentMapper;
    }

    @Override
    public Assignment save(Assignment assignment) {
        AssignmentEntity entity = assignmentMapper.toEntity(assignment);
        AssignmentEntity saved = assignmentRepository.save(entity);
        return assignmentMapper.toDomain(saved);
    }

    @Override
    public List<Assignment> findByTicketId(Long ticketId) {
        return assignmentRepository.findByTicketId(ticketId)
                .stream()
                .map(assignmentMapper::toDomain)
                .toList();
    }
}
