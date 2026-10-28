package com.helpflow.support.infrastructure.adapter.in.rest.controller;

import com.helpflow.support.application.dto.CreateTicketCommand;
import com.helpflow.support.application.port.in.AssignTicketUseCase;
import com.helpflow.support.application.port.in.CreateTicketUseCase;
import com.helpflow.support.application.port.in.GetTicketUseCase;
import com.helpflow.support.application.port.in.TransitionTicketUseCase;
import com.helpflow.support.application.port.out.TechnicianRepositoryPort;
import com.helpflow.support.application.port.out.UserRepositoryPort;
import com.helpflow.support.domain.model.Assignment;
import com.helpflow.support.domain.model.Technician;
import com.helpflow.support.domain.model.Ticket;
import com.helpflow.support.domain.model.User;
import com.helpflow.support.infrastructure.adapter.in.rest.dto.AssignmentResponse;
import com.helpflow.support.infrastructure.adapter.in.rest.dto.CreateTicketRequest;
import com.helpflow.support.infrastructure.adapter.in.rest.dto.TicketResponse;
import com.helpflow.support.infrastructure.adapter.in.rest.dto.TransitionTicketRequest;
import com.helpflow.support.infrastructure.adapter.in.rest.mapper.TicketRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/tickets")
@Tag(name = "Tickets", description = "Endpoints for ticket registration, dispatch assignment, status transitions and queries")
@SecurityRequirement(name = "bearerAuth")
public class TicketController {

    private final CreateTicketUseCase createTicketUseCase;
    private final AssignTicketUseCase assignTicketUseCase;
    private final TransitionTicketUseCase transitionTicketUseCase;
    private final GetTicketUseCase getTicketUseCase;
    private final TicketRestMapper ticketRestMapper;
    private final UserRepositoryPort userRepositoryPort;
    private final TechnicianRepositoryPort technicianRepositoryPort;

    public TicketController(CreateTicketUseCase createTicketUseCase,
                            AssignTicketUseCase assignTicketUseCase,
                            TransitionTicketUseCase transitionTicketUseCase,
                            GetTicketUseCase getTicketUseCase,
                            TicketRestMapper ticketRestMapper,
                            UserRepositoryPort userRepositoryPort,
                            TechnicianRepositoryPort technicianRepositoryPort) {
        this.createTicketUseCase = createTicketUseCase;
        this.assignTicketUseCase = assignTicketUseCase;
        this.transitionTicketUseCase = transitionTicketUseCase;
        this.getTicketUseCase = getTicketUseCase;
        this.ticketRestMapper = ticketRestMapper;
        this.userRepositoryPort = userRepositoryPort;
        this.technicianRepositoryPort = technicianRepositoryPort;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @Operation(summary = "Create a ticket", description = "Creates a new ticket in ABIERTO status with SLA calculation")
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        CreateTicketCommand command = ticketRestMapper.toCommand(request);
        Ticket created = createTicketUseCase.createTicket(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketRestMapper.toResponse(created));
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign a ticket", description = "Consults dispatch service and assigns technician with compatibility and capacity validation")
    public ResponseEntity<AssignmentResponse> assignTicket(@PathVariable Long id, Authentication authentication) {
        String assignedBy = (authentication != null) ? authentication.getName() : "SYSTEM";
        Assignment assignment = assignTicketUseCase.assignTicket(id, assignedBy);
        return ResponseEntity.ok(ticketRestMapper.toResponse(assignment));
    }

    @PatchMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('TECNICO', 'ADMIN')")
    @Operation(summary = "Start ticket progress", description = "Transitions ticket from ASIGNADO to EN_PROGRESO")
    public ResponseEntity<TicketResponse> startProgress(
            @PathVariable Long id,
            @RequestBody(required = false) TransitionTicketRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        String notes = (request != null) ? request.comments() : "Work started on ticket";
        Ticket updated = transitionTicketUseCase.startProgress(id, username, notes);
        return ResponseEntity.ok(ticketRestMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('TECNICO', 'ADMIN')")
    @Operation(summary = "Resolve ticket", description = "Transitions ticket from EN_PROGRESO to RESUELTO with resolution notes")
    public ResponseEntity<TicketResponse> resolveTicket(
            @PathVariable Long id,
            @Valid @RequestBody TransitionTicketRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        Ticket updated = transitionTicketUseCase.resolveTicket(id, username, request.comments());
        return ResponseEntity.ok(ticketRestMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Close ticket", description = "Closes a resolved ticket and releases technician active capacity")
    public ResponseEntity<TicketResponse> closeTicket(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        Ticket updated = transitionTicketUseCase.closeTicket(id, username);
        return ResponseEntity.ok(ticketRestMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @Operation(summary = "Cancel ticket", description = "Cancels ticket and releases technician active capacity if assigned")
    public ResponseEntity<TicketResponse> cancelTicket(
            @PathVariable Long id,
            @Valid @RequestBody TransitionTicketRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        Ticket updated = transitionTicketUseCase.cancelTicket(id, username, request.comments());
        return ResponseEntity.ok(ticketRestMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/escalate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Escalate ticket", description = "Escalates ticket priority when near SLA breach")
    public ResponseEntity<TicketResponse> escalateTicket(
            @PathVariable Long id,
            @RequestBody(required = false) TransitionTicketRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        String reason = (request != null) ? request.comments() : "Automated SLA escalation";
        Ticket updated = transitionTicketUseCase.escalateTicket(id, username, reason);
        return ResponseEntity.ok(ticketRestMapper.toResponse(updated));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by ID", description = "Returns full ticket details by identifier")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        Ticket ticket = getTicketUseCase.getTicketById(id);
        return ResponseEntity.ok(ticketRestMapper.toResponse(ticket));
    }

    @GetMapping
    @Operation(summary = "List tickets", description = "Lists tickets according to authenticated user role: users see their own, technicians see assigned, admins see all")
    public ResponseEntity<List<TicketResponse>> getTickets(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isTechnician = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TECNICO"));

        List<Ticket> tickets;

        if (isAdmin) {
            tickets = getTicketUseCase.getAllTickets();
        } else if (isTechnician) {
            tickets = technicianRepositoryPort.findByEmail(authentication.getName())
                    .map(Technician::getId)
                    .map(getTicketUseCase::getTicketsByTechnician)
                    .orElse(Collections.emptyList());
        } else {
            tickets = userRepositoryPort.findByCorporateEmail(authentication.getName())
                    .map(User::getId)
                    .map(getTicketUseCase::getTicketsByRequester)
                    .orElse(Collections.emptyList());
        }

        List<TicketResponse> response = tickets.stream()
                .map(ticketRestMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }
}
