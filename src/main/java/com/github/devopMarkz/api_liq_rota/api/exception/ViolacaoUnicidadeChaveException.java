package com.github.devopMarkz.api_liq_rota.api.exception;

public class ViolacaoUnicidadeChaveException extends RuntimeException {
    public ViolacaoUnicidadeChaveException(String message) {
        super(message);
    }

    public ViolacaoUnicidadeChaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
