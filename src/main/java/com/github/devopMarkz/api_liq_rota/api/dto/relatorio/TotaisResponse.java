package com.github.devopMarkz.api_liq_rota.api.dto.relatorio;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TotaisResponse {

    private final BigDecimal totalDistanciaConsideradaKm;
    private final BigDecimal totalCustoCombustivel;
    private final BigDecimal totalGastosAdicionais;
    private final BigDecimal totalGastoTotal;
    private final BigDecimal totalValorFrete;
    private final BigDecimal totalValorLiquido;
    private final BigDecimal ganhoPorKmTotal; // LiquidoTot / DistanciaTot
}
