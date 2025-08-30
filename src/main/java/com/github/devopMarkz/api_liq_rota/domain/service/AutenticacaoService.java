package com.github.devopMarkz.api_liq_rota.domain.service;

import com.github.devopMarkz.api_liq_rota.api.dto.auth.LoginDTO;
import com.github.devopMarkz.api_liq_rota.api.dto.auth.TokenDTO;
import com.github.devopMarkz.api_liq_rota.domain.model.Usuario;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AutenticacaoService(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public TokenDTO realizarLogin(LoginDTO loginDTO) {
        var authenticaticationToken = new UsernamePasswordAuthenticationToken(loginDTO.username(), loginDTO.senha());
        var authentication = authenticationManager.authenticate(authenticaticationToken);
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return tokenService.montarToken(usuario);
    }

}
