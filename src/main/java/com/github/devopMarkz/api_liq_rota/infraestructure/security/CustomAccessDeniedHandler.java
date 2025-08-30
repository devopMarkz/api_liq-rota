package com.github.devopMarkz.api_liq_rota.infraestructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.devopMarkz.api_liq_rota.api.dto.erro.ErroDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErroDTO erro = new ErroDTO(
                Instant.now().toString(),
                403,
                request.getRequestURI(),
                Collections.singletonList("Você não possui permissão para acessar este recurso.")
        );

        String json = new ObjectMapper().writeValueAsString(erro);
        response.getWriter().write(json);
    }

}