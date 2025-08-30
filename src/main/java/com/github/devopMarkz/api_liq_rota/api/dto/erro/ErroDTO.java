package com.github.devopMarkz.api_liq_rota.api.dto.erro;

import java.util.List;

public record ErroDTO(
        String timestamp,
        Integer status,
        String path,
        List<String> erros
) {
}
