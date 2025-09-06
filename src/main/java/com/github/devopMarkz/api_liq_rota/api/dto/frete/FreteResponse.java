package com.github.devopMarkz.api_liq_rota.api.dto.frete;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FreteResponse {

    private final String origem;
    private final String destino;

    private final BigDecimal distanciaConsideradaKm; // D_ef
    private final Boolean idaEVolta;

    private final BigDecimal custoCombustivel; // G_comb (persistível)
    private final BigDecimal gastosAdicionais; // G_ad
    private final BigDecimal gastoTotal;
    // G_total
    private final BigDecimal valorFrete;       // F
    private final BigDecimal valorLiquido;     // Líquido (persistível)
    private final BigDecimal ganhoPorKm;       // Líquido / D_ef (ou null)

    // duração considerando 80 km/h e distância considerada
    private final BigDecimal duracaoHoras;     // ex.: 10.50
    private final String duracaoFormatada;     // ex.: "10 h 30 min" ou "1 dia 4 h"
}
