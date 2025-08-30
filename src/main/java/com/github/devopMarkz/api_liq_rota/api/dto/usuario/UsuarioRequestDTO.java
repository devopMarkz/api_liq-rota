package com.github.devopMarkz.api_liq_rota.api.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequestDTO {

    @NotBlank(message = "Nome de usuário precisa ser informado.")
    private String username;

    @NotBlank(message = "Senha precisa ser informada.")
    private String senha;

}
