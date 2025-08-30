package com.github.devopMarkz.api_liq_rota.domain.service;

import com.github.devopMarkz.api_liq_rota.api.dto.relatorio.RelatorioAnualItem;
import com.github.devopMarkz.api_liq_rota.api.dto.relatorio.RelatorioIntervaloTotais;
import com.github.devopMarkz.api_liq_rota.api.dto.relatorio.RelatorioMensalItem;
import com.github.devopMarkz.api_liq_rota.api.exception.RelatorioParametroInvalidoException;
import com.github.devopMarkz.api_liq_rota.domain.model.Usuario;
import com.github.devopMarkz.api_liq_rota.domain.projections.RelatorioAnualProjection;
import com.github.devopMarkz.api_liq_rota.domain.projections.RelatorioMensalProjection;
import com.github.devopMarkz.api_liq_rota.domain.projections.TotaisIntervaloProjection;
import com.github.devopMarkz.api_liq_rota.domain.repository.ViagemRepository;
import com.github.devopMarkz.api_liq_rota.infraestructure.security.UsuarioAutenticadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final ViagemRepository repo;

    @Transactional(readOnly = true)
    public List<RelatorioMensalItem> mensal(int ano) {
        validarAno(ano);

        Usuario user = UsuarioAutenticadoService.getUsuarioAutenticado();

        List<RelatorioMensalProjection> rows = repo.relatorioMensal(user.getId(), ano);
        List<RelatorioMensalItem> out = new ArrayList<>(rows.size());

        for (RelatorioMensalProjection p : rows) {
            BigDecimal dist = nz(p.getTotalDistanciaKm());
            BigDecimal comb = nz(p.getTotalCombustivel());
            BigDecimal gad  = nz(p.getTotalGastosAdicionais());
            BigDecimal gtot = nz(p.getTotalGastoTotal());
            BigDecimal fre  = nz(p.getTotalValorFrete());
            BigDecimal liq  = nz(p.getTotalValorLiquido());

            BigDecimal ganhoKm = dist.compareTo(BigDecimal.ZERO) > 0
                    ? liq.divide(dist, 6, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP)
                    : null;

            out.add(RelatorioMensalItem.builder()
                    .mes(p.getMes())
                    .totalDistanciaConsideradaKm(scale2(dist))
                    .totalCustoCombustivel(scale2(comb))
                    .totalGastosAdicionais(scale2(gad))
                    .totalGastoTotal(scale2(gtot))
                    .totalValorFrete(scale2(fre))
                    .totalValorLiquido(scale2(liq))
                    .ganhoPorKmMedio(ganhoKm)
                    .build());
        }
        return out;
    }

    @Transactional(readOnly = true)
    public List<RelatorioAnualItem> anual(int de, int ate) {
        validarAnoIntervalo(de, ate);

        Usuario user = UsuarioAutenticadoService.getUsuarioAutenticado();

        List<RelatorioAnualProjection> rows = repo.relatorioAnual(user.getId(), de, ate);
        List<RelatorioAnualItem> out = new ArrayList<>(rows.size());

        for (RelatorioAnualProjection p : rows) {
            BigDecimal dist = nz(p.getTotalDistanciaKm());
            BigDecimal comb = nz(p.getTotalCombustivel());
            BigDecimal gad  = nz(p.getTotalGastosAdicionais());
            BigDecimal gtot = nz(p.getTotalGastoTotal());
            BigDecimal fre  = nz(p.getTotalValorFrete());
            BigDecimal liq  = nz(p.getTotalValorLiquido());

            BigDecimal ganhoKm = dist.compareTo(BigDecimal.ZERO) > 0
                    ? liq.divide(dist, 6, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP)
                    : null;

            out.add(RelatorioAnualItem.builder()
                    .ano(p.getAno())
                    .totalDistanciaConsideradaKm(scale2(dist))
                    .totalCustoCombustivel(scale2(comb))
                    .totalGastosAdicionais(scale2(gad))
                    .totalGastoTotal(scale2(gtot))
                    .totalValorFrete(scale2(fre))
                    .totalValorLiquido(scale2(liq))
                    .ganhoPorKmMedio(ganhoKm)
                    .build());
        }
        return out;
    }

    @Transactional(readOnly = true)
    public RelatorioIntervaloTotais intervalo(LocalDate inicio, LocalDate fim, ZoneId tz) {
        if (inicio == null) throw new RelatorioParametroInvalidoException("Data inicial (inicio) é obrigatória.");
        if (fim == null)    throw new RelatorioParametroInvalidoException("Data final (fim) é obrigatória.");
        if (inicio.isAfter(fim)) {
            throw new RelatorioParametroInvalidoException("Data inicial não pode ser maior que a data final.");
        }
        if (tz == null) throw new RelatorioParametroInvalidoException("Timezone (tz) é obrigatório.");

        Usuario user = UsuarioAutenticadoService.getUsuarioAutenticado();

        OffsetDateTime ini = inicio.atStartOfDay(tz).toOffsetDateTime();
        OffsetDateTime end = fim.plusDays(1).atStartOfDay(tz).toOffsetDateTime().minusNanos(1);

        TotaisIntervaloProjection p = repo.totaisPorIntervalo(user.getId(), ini, end);

        BigDecimal dist = nz(p == null ? null : p.getTotalDistanciaKm());
        BigDecimal comb = nz(p == null ? null : p.getTotalCombustivel());
        BigDecimal gad  = nz(p == null ? null : p.getTotalGastosAdicionais());
        BigDecimal gtot = nz(p == null ? null : p.getTotalGastoTotal());
        BigDecimal fre  = nz(p == null ? null : p.getTotalValorFrete());
        BigDecimal liq  = nz(p == null ? null : p.getTotalValorLiquido());

        BigDecimal ganhoKm = dist.compareTo(BigDecimal.ZERO) > 0
                ? liq.divide(dist, 6, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP)
                : null;

        return RelatorioIntervaloTotais.builder()
                .totalDistanciaConsideradaKm(scale2(dist))
                .totalCustoCombustivel(scale2(comb))
                .totalGastosAdicionais(scale2(gad))
                .totalGastoTotal(scale2(gtot))
                .totalValorFrete(scale2(fre))
                .totalValorLiquido(scale2(liq))
                .ganhoPorKmMedio(ganhoKm)
                .build();
    }

    /* ===== helpers ===== */

    private static BigDecimal scale2(BigDecimal bd) {
        return bd == null ? null : bd.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal nz(BigDecimal bd) {
        return bd == null ? BigDecimal.ZERO : bd;
    }

    private void validarAno(int ano) {
        if (ano < 1900 || ano > 9999) {
            throw new RelatorioParametroInvalidoException("Ano inválido: " + ano + ". Use um ano entre 1900 e 9999.");
        }
    }

    private void validarAnoIntervalo(int de, int ate) {
        if (de < 1900 || de > 9999) {
            throw new RelatorioParametroInvalidoException("Ano inicial inválido: " + de + ".");
        }
        if (ate < 1900 || ate > 9999) {
            throw new RelatorioParametroInvalidoException("Ano final inválido: " + ate + ".");
        }
        if (de > ate) {
            throw new RelatorioParametroInvalidoException("Ano inicial não pode ser maior que o ano final.");
        }
    }
}
