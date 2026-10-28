package com.helpflow.support.infrastructure.adapter.in.rest.controller;

import com.helpflow.support.application.dto.AuthResult;
import com.helpflow.support.application.dto.LoginCommand;
import com.helpflow.support.application.dto.RegisterUserCommand;
import com.helpflow.support.application.port.in.AuthenticateUserUseCase;
import com.helpflow.support.application.port.in.RegisterUserUseCase;
import com.helpflow.support.domain.model.User;
import com.helpflow.support.infrastructure.adapter.in.rest.dto.AuthResponse;
import com.helpflow.support.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.helpflow.support.infrastructure.adapter.in.rest.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration and JWT authentication")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          AuthenticateUserUseCase authenticateUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with encrypted password and validated unique document/email")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.document(),
                request.name(),
                request.corporateEmail(),
                request.password(),
                request.headquarters(),
                request.department(),
                request.role()
        );
        User createdUser = registerUserUseCase.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user credentials", description = "Authenticates user credentials and generates a stateless JWT bearer token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(request.corporateEmail(), request.password());
        AuthResult result = authenticateUserUseCase.login(command);

        AuthResponse response = new AuthResponse(
                result.token(),
                result.tokenType(),
                result.userId(),
                result.corporateEmail(),
                result.role()
        );
        return ResponseEntity.ok(response);
    }
}
