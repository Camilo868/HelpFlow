package com.helpflow.dispatch.infrastructure.adapter.in.rest;

import com.helpflow.dispatch.application.service.DeterministicDispatchService;
import com.helpflow.dispatch.domain.model.DispatchRecommendation;
import com.helpflow.dispatch.infrastructure.adapter.in.rest.dto.DispatchRequest;
import com.helpflow.dispatch.infrastructure.adapter.in.rest.dto.DispatchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/technician-dispatch")
@Tag(name = "Technician Dispatch", description = "Deterministic technician assignment and availability calculation service")
public class DispatchController {

    private final DeterministicDispatchService dispatchService;

    public DispatchController(DeterministicDispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @PostMapping
    @Operation(
            summary = "Request deterministic technician dispatch",
            description = "Calculates a suggested technician and estimated attention time deterministically based on ticketId, category and priority"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Technician dispatch recommendation successfully generated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DispatchResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters (validation error)",
                    content = @Content)
    })
    public ResponseEntity<DispatchResponse> requestDispatch(@Valid @RequestBody DispatchRequest request) {
        DispatchRecommendation rec = dispatchService.dispatch(
                request.ticketId(),
                request.categoria(),
                request.prioridad()
        );

        DispatchResponse response = new DispatchResponse(
                rec.getTicketId(),
                rec.getTechnicianDocument(),
                rec.getAvailabilityLevel().name(),
                rec.getEstimatedAvailabilityHours(),
                rec.getTechnicianName(),
                rec.getSpecialty(),
                rec.getDispatchReason()
        );

        return ResponseEntity.ok(response);
    }
}
