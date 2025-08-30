package com.github.devopMarkz.api_liq_rota.infraestructure.security;

import com.github.devopMarkz.api_liq_rota.domain.model.Usuario;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioAutenticadoService {

    public static Usuario getUsuarioAutenticado() {
        return (Usuario) SecurityContextHolder.
                getContext()
                .getAuthentication()
                .getPrincipal();
    }

}
