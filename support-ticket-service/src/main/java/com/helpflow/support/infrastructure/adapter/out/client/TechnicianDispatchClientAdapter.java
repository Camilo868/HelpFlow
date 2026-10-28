package com.helpflow.support.infrastructure.adapter.out.client;

import com.helpflow.support.application.dto.DispatchResult;
import com.helpflow.support.application.port.out.TechnicianDispatchPort;
import com.helpflow.support.domain.enums.TechnicianAvailability;
import com.helpflow.support.domain.enums.TicketCategory;
import com.helpflow.support.domain.enums.TicketPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class TechnicianDispatchClientAdapter implements TechnicianDispatchPort {

    private static final Logger log = LoggerFactory.getLogger(TechnicianDispatchClientAdapter.class);

    private final RestClient restClient;
    private final String dispatchServiceUrl;

    public TechnicianDispatchClientAdapter(
            @Value("${technician.dispatch.service.url:http://localhost:8081}") String dispatchServiceUrl
    ) {
        this.dispatchServiceUrl = dispatchServiceUrl;
        this.restClient = RestClient.builder()
                .baseUrl(dispatchServiceUrl)
                .build();
    }

    @Override
    public Optional<DispatchResult> requestDispatch(Long ticketId, TicketCategory category, TicketPriority priority) {
        try {
            DispatchApiRequest requestBody = new DispatchApiRequest(
                    ticketId,
                    category.name(),
                    priority.name()
            );

            DispatchApiResponse response = restClient.post()
                    .uri("/technician-dispatch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(DispatchApiResponse.class);

            if (response != null) {
                TechnicianAvailability availability;
                try {
                    availability = TechnicianAvailability.valueOf(response.disponibilidad());
                } catch (Exception e) {
                    availability = TechnicianAvailability.MEDIA;
                }

                return Optional.of(new DispatchResult(
                        response.ticketId(),
                        response.tecnicoId(),
                        availability,
                        response.tiempoEstimadoHoras()
                ));
            }
        } catch (Exception ex) {
            log.warn("Technician dispatch service at {} is currently unreachable or returned error: {}. Proceeding with local technician assignment rules.",
                    dispatchServiceUrl, ex.getMessage());
        }

        return Optional.empty();
    }

    private record DispatchApiRequest(Long ticketId, String categoria, String prioridad) {
    }

    private record DispatchApiResponse(Long ticketId, String tecnicoId, String disponibilidad, int tiempoEstimadoHoras) {
    }
}
