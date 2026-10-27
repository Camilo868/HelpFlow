package com.helpflow.support.infrastructure.adapter.out.persistence.mapper;

import com.helpflow.support.domain.model.Ticket;
import com.helpflow.support.infrastructure.adapter.out.persistence.entity.TicketEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserPersistenceMapper.class, TechnicianPersistenceMapper.class})
public interface TicketPersistenceMapper {

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "assignedTechnicianId", source = "assignedTechnician.id")
    Ticket toDomain(TicketEntity entity);

    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "assignedTechnician", ignore = true)
    @Mapping(target = "historials", ignore = true)
    TicketEntity toEntity(Ticket domain);
}
