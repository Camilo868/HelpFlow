package com.helpflow.support.application.port.in;

import com.helpflow.support.application.dto.AuthResult;
import com.helpflow.support.application.dto.LoginCommand;

public interface AuthenticateUserUseCase {

    AuthResult login(LoginCommand command);
}
