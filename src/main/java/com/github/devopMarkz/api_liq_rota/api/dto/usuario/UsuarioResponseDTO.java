package com.github.devopMarkz.api_liq_rota.api.dto.usuario;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.devopMarkz.api_liq_rota.domain.model.Perfil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioResponseDTO {

    private Long id;
    private String username;
    private Perfil perfil;
    private Boolean ativo;

}
