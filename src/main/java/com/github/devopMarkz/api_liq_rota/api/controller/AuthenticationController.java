package com.github.devopMarkz.api_liq_rota.api.controller;

import com.github.devopMarkz.api_liq_rota.api.dto.auth.LoginDTO;
import com.github.devopMarkz.api_liq_rota.api.dto.auth.TokenDTO;
import com.github.devopMarkz.api_liq_rota.domain.service.AutenticacaoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AutenticacaoService autenticacaoService;

    public AuthenticationController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request){
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        String userAgent = request.getHeader("User-Agent");

        System.out.println("Origin: " + origin);
        System.out.println("Referer: " + referer);
        System.out.println("User-Agent: " + userAgent);

        TokenDTO tokenDTO = autenticacaoService.realizarLogin(loginDTO);
        return ResponseEntity.ok(tokenDTO);
    }
}