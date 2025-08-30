package com.github.devopMarkz.api_liq_rota.domain.projections;

import java.math.BigDecimal;

public interface TotaisIntervaloProjection {
    BigDecimal getTotalDistanciaKm();
    BigDecimal getTotalCombustivel();
    BigDecimal getTotalGastosAdicionais();
    BigDecimal getTotalGastoTotal();
    BigDecimal getTotalValorFrete();
    BigDecimal getTotalValorLiquido();
}