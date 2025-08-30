package com.github.devopMarkz.api_liq_rota.infraestructure.mapper;

import com.github.devopMarkz.api_liq_rota.api.dto.viagem.ViagemResponseDTO;
import com.github.devopMarkz.api_liq_rota.domain.model.Viagem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", uses = { UsuarioMapper.class })
public interface ViagemMapper {

    @Mapping(target = "usuario.ativo", ignore = true)
    ViagemResponseDTO toResponse(Viagem entity);

    List<ViagemResponseDTO> toResponseList(List<Viagem> entities);

    default Page<ViagemResponseDTO> toResponsePage(Page<Viagem> page) {
        return page.map(this::toResponse);
    }

}
