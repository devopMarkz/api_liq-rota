package com.github.devopMarkz.api_liq_rota.api.dto.frete;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
public class FreteRequest {

    @NotBlank
    private String origem;

    @NotBlank
    private String destino;

    @NotNull @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal distanciaKm;

    @NotNull @DecimalMin(value = "0.000001", inclusive = true,
            message = "consumoKmPorLitro deve ser > 0")
    private BigDecimal consumoKmPorLitro;

    @NotNull @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal precoLitro;

    @NotNull @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal gastosAdicionais;

    @NotNull @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal valorFrete;

    private Boolean idaEVolta = Boolean.FALSE;
}
