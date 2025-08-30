package com.github.devopMarkz.api_liq_rota.api.controller;

import com.github.devopMarkz.api_liq_rota.api.dto.frete.FreteLoteRequest;
import com.github.devopMarkz.api_liq_rota.api.dto.frete.FreteRequest;
import com.github.devopMarkz.api_liq_rota.api.dto.frete.FreteResponse;
import com.github.devopMarkz.api_liq_rota.domain.model.Viagem;
import com.github.devopMarkz.api_liq_rota.domain.service.CalculoFreteService;
import com.github.devopMarkz.api_liq_rota.utils.GerenciadorDePermissoes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class ViagemController {

    private final CalculoFreteService service;

    @PostMapping
    @PreAuthorize(GerenciadorDePermissoes.ROLE_USUARIO_COMUM)
    public ResponseEntity<Viagem> criar(@Valid @RequestBody FreteRequest request) {
        return ResponseEntity.status(201).body(service.criarViagem(request));
    }

    @PostMapping("/lote")
    @PreAuthorize(GerenciadorDePermissoes.ROLE_USUARIO_COMUM)
    public ResponseEntity<List<Viagem>> criarLote(@Valid @RequestBody FreteLoteRequest request) {
        return ResponseEntity.status(201).body(service.criarViagensEmLote(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize(GerenciadorDePermissoes.ROLE_USUARIO_COMUM)
    public ResponseEntity<Viagem> obter(@PathVariable Long id) {
        return ResponseEntity.ok(service.obterViagem(id));
    }

    @GetMapping
    @PreAuthorize(GerenciadorDePermissoes.ROLE_USUARIO_COMUM)
    public ResponseEntity<Page<Viagem>> listar(
            @RequestParam(required = false) String origem,
            @RequestParam(required = false) String destino,
            Pageable pageable) {
        return ResponseEntity.ok(service.listarViagens(origem, destino, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize(GerenciadorDePermissoes.ROLE_USUARIO_COMUM)
    public ResponseEntity<Viagem> atualizar(@PathVariable Long id, @Valid @RequestBody FreteRequest request) {
        return ResponseEntity.ok(service.atualizarViagem(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(GerenciadorDePermissoes.ROLE_USUARIO_COMUM)
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.removerViagem(id);
        return ResponseEntity.noContent().build();
    }

    // Recalcula e retorna o payload (sem alterar dados persistidos)
    @PostMapping("/{id}/calcular")
    @PreAuthorize(GerenciadorDePermissoes.ROLE_USUARIO_COMUM)
    public ResponseEntity<FreteResponse> recalcular(@PathVariable Long id) {
        Viagem v = service.obterViagem(id);
        FreteRequest req = new FreteRequest();
        req.setOrigem(v.getOrigem());
        req.setDestino(v.getDestino());
        req.setDistanciaKm(v.getDistanciaKm());
        req.setConsumoKmPorLitro(v.getConsumoKmPorLitro());
        req.setPrecoLitro(v.getPrecoLitro());
        req.setGastosAdicionais(v.getGastosAdicionais());
        req.setValorFrete(v.getValorFrete());
        req.setIdaEVolta(v.getIdaEVolta());
        return ResponseEntity.ok(service.calcular(req));
    }
}