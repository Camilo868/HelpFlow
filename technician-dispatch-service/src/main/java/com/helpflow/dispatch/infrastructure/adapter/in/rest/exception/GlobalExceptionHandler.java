package com.helpflow.dispatch.infrastructure.adapter.in.rest.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                errors,
                URI.create("https://helpflow.com/errors/validation-error"),
                request
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage(),
                URI.create("https://helpflow.com/errors/bad-request"),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, WebRequest request) {
        log.error("Unhandled exception in technician-dispatch-service", ex);
        return buildProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected internal error occurred: " + ex.getMessage(),
                URI.create("https://helpflow.com/errors/internal-error"),
                request
        );
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String title, String detail, URI type, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(type);
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("traceId", UUID.randomUUID().toString());
        problem.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));

        log.warn("Error handled [status={}, title={}, uri={}]",
                status.value(), title, problem.getInstance());

        return problem;
    }
}
