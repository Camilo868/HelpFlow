package com.helpflow.support.infrastructure.adapter.out.persistence.adapter;

import com.helpflow.support.application.port.out.TicketHistoryRepositoryPort;
import com.helpflow.support.domain.model.TicketHistory;
import com.helpflow.support.infrastructure.adapter.out.persistence.entity.TicketEntity;
import com.helpflow.support.infrastructure.adapter.out.persistence.entity.TicketHistoryEntity;
import com.helpflow.support.infrastructure.adapter.out.persistence.mapper.TicketHistoryPersistenceMapper;
import com.helpflow.support.infrastructure.adapter.out.persistence.repository.SpringDataJpaTicketHistoryRepository;
import com.helpflow.support.infrastructure.adapter.out.persistence.repository.SpringDataJpaTicketRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketHistoryPersistenceAdapter implements TicketHistoryRepositoryPort {

    private final SpringDataJpaTicketHistoryRepository historyRepository;
    private final SpringDataJpaTicketRepository ticketRepository;
    private final TicketHistoryPersistenceMapper historyMapper;

    public TicketHistoryPersistenceAdapter(SpringDataJpaTicketHistoryRepository historyRepository,
                                          SpringDataJpaTicketRepository ticketRepository,
                                          TicketHistoryPersistenceMapper historyMapper) {
        this.historyRepository = historyRepository;
        this.ticketRepository = ticketRepository;
        this.historyMapper = historyMapper;
    }

    @Override
    public TicketHistory save(TicketHistory ticketHistory) {
        TicketHistoryEntity entity = historyMapper.toEntity(ticketHistory);

        if (ticketHistory.getTicketId() != null) {
            TicketEntity ticket = ticketRepository.getReferenceById(ticketHistory.getTicketId());
            entity.setTicket(ticket);
        }

        TicketHistoryEntity saved = historyRepository.save(entity);
        return historyMapper.toDomain(saved);
    }

    @Override
    public List<TicketHistory> findByTicketId(Long ticketId) {
        return historyRepository.findByTicketIdOrderByChangeDateDesc(ticketId)
                .stream()
                .map(historyMapper::toDomain)
                .toList();
    }
}
