package com.helpflow.support.application.port.out;

import com.helpflow.support.domain.model.Assignment;

import java.util.List;

public interface AssignmentRepositoryPort {

    Assignment save(Assignment assignment);

    List<Assignment> findByTicketId(Long ticketId);
}
