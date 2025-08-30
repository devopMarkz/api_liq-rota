package com.github.devopMarkz.api_liq_rota.infraestructure.security;

import com.github.devopMarkz.api_liq_rota.api.exception.TokenInvalidoException;
import com.github.devopMarkz.api_liq_rota.api.exception.UsuarioInativoException;
import com.github.devopMarkz.api_liq_rota.domain.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public CustomAuthenticationFilter(TokenService tokenService,
                                      CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.tokenService = tokenService;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = obterTokenDoHeader(request);

            if (token != null) {
                var usuario = tokenService.validarToken(token);

                if (usuario.getAtivo()) {
                    var authentication = new UsernamePasswordAuthenticationToken(
                            usuario,
                            null,
                            usuario.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new UsuarioInativoException("Usuário inativo");
                }
            }
        } catch (UsuarioInativoException | TokenInvalidoException e) {
            customAuthenticationEntryPoint.commence(request, response, e);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String obterTokenDoHeader(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");

        if(bearerToken == null || !bearerToken.startsWith("Bearer")){
            return null;
        }

        return bearerToken.split(" ")[1];
    }
}
