package com.github.devopMarkz.api_liq_rota.domain.projections;

import java.math.BigDecimal;

public interface RelatorioMensalProjection {
    String getMes(); // "YYYY-MM"
    BigDecimal getTotalDistanciaKm();
    BigDecimal getTotalCombustivel();
    BigDecimal getTotalGastosAdicionais();
    BigDecimal getTotalGastoTotal();
    BigDecimal getTotalValorFrete();
    BigDecimal getTotalValorLiquido();
}