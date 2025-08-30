package com.github.devopMarkz.api_liq_rota.api.exception;

public class EntidadeInexistenteException extends RuntimeException {
    public EntidadeInexistenteException(String message) {
        super(message);
    }

    public EntidadeInexistenteException(String message, Throwable cause) {
        super(message, cause);
    }
}
