package com.github.devopMarkz.api_liq_rota.api.dto.frete;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class FreteRequest {

    @NotBlank
    private String origem;

    @NotBlank
    private String destino;

    @NotNull @DecimalMin(value = "0.0")
    private BigDecimal distanciaKm;

    @NotNull @DecimalMin(value = "0.000001")
    private BigDecimal consumoKmPorLitro;

    @NotNull @DecimalMin(value = "0.0")
    private BigDecimal precoLitro;

    @NotNull @DecimalMin(value = "0.0")
    private BigDecimal gastosAdicionais;

    // MODO A: informar diretamente o valor do frete
    @DecimalMin(value = "0.0")
    private BigDecimal valorFrete; // opcional agora

    // MODO B: informar quanto QUER ganhar por km (líquido/km)
    @DecimalMin(value = "0.0")
    private BigDecimal ganhoPorKmDesejado; // opcional

    private Boolean idaEVolta = Boolean.FALSE;
}
