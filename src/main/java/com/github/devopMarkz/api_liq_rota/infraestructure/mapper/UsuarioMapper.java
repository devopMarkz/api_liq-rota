package com.github.devopMarkz.api_liq_rota.infraestructure.mapper;

import com.github.devopMarkz.api_liq_rota.api.dto.usuario.UsuarioRequestDTO;
import com.github.devopMarkz.api_liq_rota.api.dto.usuario.UsuarioResponseDTO;
import com.github.devopMarkz.api_liq_rota.domain.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class UsuarioMapper {

    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "perfil", ignore = true)
    public abstract Usuario toUsuario(UsuarioRequestDTO requestDTO);

    public abstract UsuarioResponseDTO toUsuarioResponseDTO(Usuario usuario);

}
