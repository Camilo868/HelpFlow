package com.helpflow.support.infrastructure.adapter.in.rest.exception;

import com.helpflow.support.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return buildProblemDetail(HttpStatus.NOT_FOUND, "Entity Not Found", ex.getMessage(), request, null);
    }

    @ExceptionHandler(InvalidTicketTransitionException.class)
    public ProblemDetail handleInvalidTransition(InvalidTicketTransitionException ex, HttpServletRequest request) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST, "Invalid Ticket Transition", ex.getMessage(), request, null);
    }

    @ExceptionHandler(InactiveUserException.class)
    public ProblemDetail handleInactiveUser(InactiveUserException ex, HttpServletRequest request) {
        return buildProblemDetail(HttpStatus.FORBIDDEN, "Inactive User", ex.getMessage(), request, null);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        return buildProblemDetail(HttpStatus.CONFLICT, "Resource Conflict", ex.getMessage(), request, null);
    }

    @ExceptionHandler(TechnicianCapacityExceededException.class)
    public ProblemDetail handleCapacityExceeded(TechnicianCapacityExceededException ex, HttpServletRequest request) {
        return buildProblemDetail(HttpStatus.CONFLICT, "Technician Capacity Exceeded", ex.getMessage(), request, null);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ProblemDetail handleAuthenticationFailed(AuthenticationFailedException ex, HttpServletRequest request) {
        return buildProblemDetail(HttpStatus.UNAUTHORIZED, "Authentication Failed", ex.getMessage(), request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildProblemDetail(HttpStatus.FORBIDDEN, "Access Denied", "You do not have permission to access this resource", request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> extra = Map.of("invalidFields", errors);
        return buildProblemDetail(HttpStatus.BAD_REQUEST, "Validation Error", "Request payload validation failed", request, extra);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled internal server error: ", ex);
        return buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred", request, null);
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String title, String detail,
                                             HttpServletRequest request, Map<String, Object> extraProperties) {
        String traceId = UUID.randomUUID().toString();
        Instant timestamp = Instant.now();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("https://helpflow.com/errors/" + title.toLowerCase().replace(" ", "-")));
        problem.setInstance(URI.create(request.getRequestURI()));

        problem.setProperty("timestamp", timestamp.toString());
        problem.setProperty("traceId", traceId);

        if (extraProperties != null) {
            extraProperties.forEach(problem::setProperty);
        }

        log.warn("Error handled [status={}, title={}, traceId={}, uri={}]", status.value(), title, traceId, request.getRequestURI());
        return problem;
    }
}
