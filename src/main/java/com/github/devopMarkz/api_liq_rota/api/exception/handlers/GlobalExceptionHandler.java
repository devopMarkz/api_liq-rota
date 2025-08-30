package com.github.devopMarkz.api_liq_rota.api.exception.handlers;

import com.github.devopMarkz.api_liq_rota.api.dto.erro.ErroDTO;
import com.github.devopMarkz.api_liq_rota.api.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<ErroDTO> handlerTokenInvalido(TokenInvalidoException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        List<String> erros = List.of(e.getMessage());
        ErroDTO erroDTO = new ErroDTO(Instant.now().toString(), status.value(), request.getRequestURI(), erros);
        return ResponseEntity.status(status).body(erroDTO);
    }

    @ExceptionHandler(UsuarioInativoException.class)
    public ResponseEntity<ErroDTO> handlerUsuarioInativo(UsuarioInativoException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        List<String> erros = List.of(e.getMessage());
        ErroDTO erroDTO = new ErroDTO(Instant.now().toString(), status.value(), request.getRequestURI(), erros);
        return ResponseEntity.status(status).body(erroDTO);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErroDTO> handlerUsernameNotFound(UsernameNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        List<String> erros = List.of(e.getMessage());
        ErroDTO erroDTO = new ErroDTO(Instant.now().toString(), status.value(), request.getRequestURI(), erros);
        return ResponseEntity.status(status).body(erroDTO);
    }

    @ExceptionHandler(EntidadeInexistenteException.class)
    public ResponseEntity<ErroDTO> handlerEntidadeInexistente(EntidadeInexistenteException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        List<String> erros = List.of(e.getMessage());
        ErroDTO erroDTO = new ErroDTO(Instant.now().toString(), status.value(), request.getRequestURI(), erros);
        return ResponseEntity.status(status).body(erroDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroDTO> handlerMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> erros = new ArrayList<>();

        for (FieldError error : fieldErrors) {
            erros.add(error.getField() + ": " + error.getDefaultMessage());
        }

        ErroDTO erroDTO = new ErroDTO(Instant.now().toString(), status.value(), request.getRequestURI(), erros);
        return ResponseEntity.status(status).body(erroDTO);
    }

    @ExceptionHandler(CalculoInvalidoException.class)
    public ResponseEntity<ErroDTO> handlerCalculoInvalido(CalculoInvalidoException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ErroDTO dto = new ErroDTO(Instant.now().toString(), status.value(), request.getRequestURI(), List.of(e.getMessage()));
        return ResponseEntity.status(status).body(dto);
    }

    @ExceptionHandler(LoteVazioException.class)
    public ResponseEntity<ErroDTO> handlerLoteVazio(LoteVazioException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ErroDTO dto = new ErroDTO(Instant.now().toString(), status.value(), request.getRequestURI(), List.of(e.getMessage()));
        return ResponseEntity.status(status).body(dto);
    }

    @ExceptionHandler(RelatorioParametroInvalidoException.class)
    public ResponseEntity<ErroDTO> handlerRelatorioParametroInvalido(RelatorioParametroInvalidoException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErroDTO erroDTO = new ErroDTO(
                java.time.Instant.now().toString(),
                status.value(),
                request.getRequestURI(),
                java.util.List.of(e.getMessage())
        );
        return ResponseEntity.status(status).body(erroDTO);
    }

}
