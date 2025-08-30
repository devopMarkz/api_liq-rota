package com.github.devopMarkz.api_liq_rota.api.controller;

import com.github.devopMarkz.api_liq_rota.api.dto.relatorio.RelatorioAnualItem;
import com.github.devopMarkz.api_liq_rota.api.dto.relatorio.RelatorioIntervaloTotais;
import com.github.devopMarkz.api_liq_rota.api.dto.relatorio.RelatorioMensalItem;
import com.github.devopMarkz.api_liq_rota.domain.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class RelatoriosController {

    private final RelatorioService service;

    @GetMapping("/mensal")
    public ResponseEntity<List<RelatorioMensalItem>> mensal(@RequestParam int ano) {
        return ResponseEntity.ok(service.mensal(ano));
    }

    @GetMapping("/anual")
    public ResponseEntity<List<RelatorioAnualItem>> anual(@RequestParam int de, @RequestParam int ate) {
        return ResponseEntity.ok(service.anual(de, ate));
    }

    @GetMapping("/intervalo")
    public ResponseEntity<RelatorioIntervaloTotais> intervalo(
            @RequestParam String inicio, @RequestParam String fim,
            @RequestParam(defaultValue = "America/Sao_Paulo") String timezone
    ) {
        LocalDate ini = LocalDate.parse(inicio);
        LocalDate end = LocalDate.parse(fim);
        return ResponseEntity.ok(service.intervalo(ini, end, ZoneId.of(timezone)));
    }
}