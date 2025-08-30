package com.github.devopMarkz.api_liq_rota.api.dto.relatorio;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class RelatorioAnualItem {

    private final Integer ano;

    private final BigDecimal totalDistanciaConsideradaKm;
    private final BigDecimal totalCustoCombustivel;
    private final BigDecimal totalGastosAdicionais;
    private final BigDecimal totalGastoTotal;
    private final BigDecimal totalValorFrete;
    private final BigDecimal totalValorLiquido;
    private final BigDecimal ganhoPorKmMedio;
}