package com.github.devopMarkz.api_liq_rota.api.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenInvalidoException extends AuthenticationException {
    public TokenInvalidoException(String message) {
        super(message);
    }

    public TokenInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
