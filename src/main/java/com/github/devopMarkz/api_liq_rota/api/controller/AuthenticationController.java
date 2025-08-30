package com.github.devopMarkz.api_liq_rota.api.controller;

import com.github.devopMarkz.api_liq_rota.api.dto.auth.LoginDTO;
import com.github.devopMarkz.api_liq_rota.api.dto.auth.TokenDTO;
import com.github.devopMarkz.api_liq_rota.domain.service.AutenticacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AutenticacaoService autenticacaoService;

    public AuthenticationController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO){
        TokenDTO tokenDTO = autenticacaoService.realizarLogin(loginDTO);
        return ResponseEntity.ok(tokenDTO);
    }
}
