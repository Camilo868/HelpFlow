package com.helpflow.support.infrastructure.adapter.in.rest.mapper;

import com.helpflow.support.application.dto.CreateTicketCommand;
import com.helpflow.support.domain.model.Assignment;
import com.helpflow.support.domain.model.Ticket;
import com.helpflow.support.infrastructure.adapter.in.rest.dto.AssignmentResponse;
import com.helpflow.support.infrastructure.adapter.in.rest.dto.CreateTicketRequest;
import com.helpflow.support.infrastructure.adapter.in.rest.dto.TicketResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketRestMapper {

    CreateTicketCommand toCommand(CreateTicketRequest request);

    TicketResponse toResponse(Ticket ticket);

    AssignmentResponse toResponse(Assignment assignment);
}
