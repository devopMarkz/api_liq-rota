package com.github.devopMarkz.api_liq_rota.api.dto.frete;

import com.github.devopMarkz.api_liq_rota.api.dto.relatorio.TotaisResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FreteLoteResponse {

    private final List<FreteResponse> itens; // resultados por viagem
    private final TotaisResponse totais;     // consolidados
}