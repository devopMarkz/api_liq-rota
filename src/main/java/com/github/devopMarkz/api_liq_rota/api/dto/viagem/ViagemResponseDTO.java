package com.github.devopMarkz.api_liq_rota.api.dto.viagem;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.devopMarkz.api_liq_rota.api.dto.usuario.UsuarioResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViagemResponseDTO {
    private Long id;

    private UsuarioResponseDTO usuario;

    private String origem;
    private String destino;

    private BigDecimal distanciaKm;
    private BigDecimal consumoKmPorLitro;
    private BigDecimal precoLitro;
    private BigDecimal gastosAdicionais;
    private BigDecimal valorFrete;
    private Boolean idaEVolta;

    private BigDecimal custoCombustivel;
    private BigDecimal valorLiquido;

    private OffsetDateTime createdAt;

    private BigDecimal duracaoHoras;    // total em horas
    private String duracaoFormatada;    // ex: "10 h 30 min" ou "1 dia 4 h"
}