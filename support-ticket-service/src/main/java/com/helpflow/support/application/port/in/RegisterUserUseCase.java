package com.helpflow.support.application.port.in;

import com.helpflow.support.application.dto.RegisterUserCommand;
import com.helpflow.support.domain.model.User;

public interface RegisterUserUseCase {

    User register(RegisterUserCommand command);
}
