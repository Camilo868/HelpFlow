package com.helpflow.support.application.service;

import com.helpflow.support.application.dto.AuthResult;
import com.helpflow.support.application.dto.LoginCommand;
import com.helpflow.support.application.dto.RegisterUserCommand;
import com.helpflow.support.application.port.in.AuthenticateUserUseCase;
import com.helpflow.support.application.port.in.RegisterUserUseCase;
import com.helpflow.support.application.port.out.PasswordEncoderPort;
import com.helpflow.support.application.port.out.TokenProviderPort;
import com.helpflow.support.application.port.out.UserRepositoryPort;
import com.helpflow.support.domain.exception.AuthenticationFailedException;
import com.helpflow.support.domain.exception.InactiveUserException;
import com.helpflow.support.domain.exception.UserAlreadyExistsException;
import com.helpflow.support.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService implements RegisterUserUseCase, AuthenticateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenProviderPort tokenProviderPort;

    public AuthService(UserRepositoryPort userRepositoryPort,
                       PasswordEncoderPort passwordEncoderPort,
                       TokenProviderPort tokenProviderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenProviderPort = tokenProviderPort;
    }

    @Override
    public User register(RegisterUserCommand command) {
        if (userRepositoryPort.existsByDocument(command.document())) {
            throw new UserAlreadyExistsException("A user already exists with document: " + command.document());
        }
        if (userRepositoryPort.existsByCorporateEmail(command.corporateEmail())) {
            throw new UserAlreadyExistsException("A user already exists with corporate email: " + command.corporateEmail());
        }

        String hashedPassword = passwordEncoderPort.encode(command.password());

        User newUser = User.createNew(
                command.document(),
                command.name(),
                command.corporateEmail(),
                hashedPassword,
                command.headquarters(),
                command.department(),
                command.role()
        );

        return userRepositoryPort.save(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResult login(LoginCommand command) {
        User user = userRepositoryPort.findByCorporateEmail(command.corporateEmail())
                .orElseThrow(() -> new AuthenticationFailedException("Invalid credentials for email: " + command.corporateEmail()));

        if (!user.isActive()) {
            throw new InactiveUserException("User account is inactive: " + command.corporateEmail());
        }

        if (!passwordEncoderPort.matches(command.password(), user.getPassword())) {
            throw new AuthenticationFailedException("Invalid credentials for email: " + command.corporateEmail());
        }

        String token = tokenProviderPort.generateToken(user.getCorporateEmail(), user.getRole().name());

        return new AuthResult(token, "Bearer", user.getId(), user.getCorporateEmail(), user.getRole().name());
    }
}
