package com.github.devopMarkz.api_liq_rota.api.dto.usuario;

import com.github.devopMarkz.api_liq_rota.domain.model.Perfil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String username;
    private String senha;
    private Perfil perfil;
    private Boolean ativo;

}
