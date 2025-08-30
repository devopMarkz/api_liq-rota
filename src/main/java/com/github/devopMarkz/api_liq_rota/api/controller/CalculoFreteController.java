package com.github.devopMarkz.api_liq_rota.api.controller;

import com.github.devopMarkz.api_liq_rota.api.dto.frete.FreteLoteRequest;
import com.github.devopMarkz.api_liq_rota.api.dto.frete.FreteLoteResponse;
import com.github.devopMarkz.api_liq_rota.api.dto.frete.FreteRequest;
import com.github.devopMarkz.api_liq_rota.api.dto.frete.FreteResponse;
import com.github.devopMarkz.api_liq_rota.domain.service.CalculoFreteService;
import com.github.devopMarkz.api_liq_rota.utils.GerenciadorDePermissoes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/calculos")
@RequiredArgsConstructor
public class CalculoFreteController {

    private final CalculoFreteService service;

    /**
     * Simula o cálculo de UMA viagem (não persiste).
     * Retorna FreteResponse com: distância considerada, custo combustível,
     * gastos adicionais, gasto total, valor frete, valor líquido e ganho por km.
     */
    @PostMapping("/frete")
    @PreAuthorize(GerenciadorDePermissoes.ROLE_USUARIO_COMUM)
    public ResponseEntity<FreteResponse> calcular(@Valid @RequestBody FreteRequest request) {
        return ResponseEntity.ok(service.calcular(request));
    }

    /**
     * Simula o cálculo de um LOTE de viagens (não persiste).
     * Retorna a lista de itens calculados + um objeto "totais" com:
     * totalDistanciaConsideradaKm, totalCustoCombustivel, totalGastosAdicionais,
     * totalGastoTotal, totalValorFrete, totalValorLiquido e ganhoPorKmTotal.
     */
    @PostMapping("/frete/lote")
    @PreAuthorize(GerenciadorDePermissoes.ROLE_USUARIO_COMUM)
    public ResponseEntity<FreteLoteResponse> calcularLote(@Valid @RequestBody FreteLoteRequest request) {
        return ResponseEntity.ok(service.calcularLote(request));
    }
}