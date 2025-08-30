package com.github.devopMarkz.api_liq_rota.domain.repository;

import com.github.devopMarkz.api_liq_rota.domain.model.Viagem;
import com.github.devopMarkz.api_liq_rota.domain.projections.RelatorioAnualProjection;
import com.github.devopMarkz.api_liq_rota.domain.projections.RelatorioMensalProjection;
import com.github.devopMarkz.api_liq_rota.domain.projections.TotaisIntervaloProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ViagemRepository extends JpaRepository<Viagem, Long> {

    Optional<Viagem> findByIdAndUsuarioId(Long id, Long usuarioId);

    Page<Viagem> findByUsuarioIdAndOrigemContainingIgnoreCaseAndDestinoContainingIgnoreCase(
            Long usuarioId, String origem, String destino, Pageable pageable
    );

    // --------- Relatórios (NATIVOS, com filtro por usuário) ---------

    // MENSAL
    @Query(value = """
      select
        to_char(date_trunc('month', v.created_at), 'YYYY-MM') as mes,
        coalesce(sum(v.distancia_km * (case when v.ida_e_volta then 2 else 1 end)), 0) as totalDistanciaKm,
        coalesce(sum(v.custo_combustivel), 0) as totalCombustivel,
        coalesce(sum(v.gastos_adicionais), 0) as totalGastosAdicionais,
        coalesce(sum(v.custo_combustivel + v.gastos_adicionais), 0) as totalGastoTotal,
        coalesce(sum(v.valor_frete), 0) as totalValorFrete,
        coalesce(sum(v.valor_liquido), 0) as totalValorLiquido
      from viagens v
      where v.usuario_id = :usuarioId
        and extract(year from v.created_at) = :ano
      group by date_trunc('month', v.created_at)
      order by date_trunc('month', v.created_at)
    """, nativeQuery = true)
    List<RelatorioMensalProjection> relatorioMensal(Long usuarioId, int ano);

    // ANUAL
    @Query(value = """
      select
        cast(extract(year from v.created_at) as int) as ano,
        coalesce(sum(v.distancia_km * (case when v.ida_e_volta then 2 else 1 end)), 0) as totalDistanciaKm,
        coalesce(sum(v.custo_combustivel), 0) as totalCombustivel,
        coalesce(sum(v.gastos_adicionais), 0) as totalGastosAdicionais,
        coalesce(sum(v.custo_combustivel + v.gastos_adicionais), 0) as totalGastoTotal,
        coalesce(sum(v.valor_frete), 0) as totalValorFrete,
        coalesce(sum(v.valor_liquido), 0) as totalValorLiquido
      from viagens v
      where v.usuario_id = :usuarioId
        and extract(year from v.created_at) between :de and :ate
      group by extract(year from v.created_at)
      order by extract(year from v.created_at)
    """, nativeQuery = true)
    List<RelatorioAnualProjection> relatorioAnual(Long usuarioId, int de, int ate);

    @Query(value = """
      select
        coalesce(sum(distancia_km * (case when ida_e_volta then 2 else 1 end)), 0) as totalDistanciaKm,
        coalesce(sum(custo_combustivel), 0)                                       as totalCombustivel,
        coalesce(sum(gastos_adicionais), 0)                                       as totalGastosAdicionais,
        coalesce(sum(custo_combustivel + gastos_adicionais), 0)                   as totalGastoTotal,
        coalesce(sum(valor_frete), 0)                                             as totalValorFrete,
        coalesce(sum(valor_liquido), 0)                                           as totalValorLiquido
      from viagens
      where usuario_id = :usuarioId
        and created_at between :inicio and :fim
    """, nativeQuery = true)
    TotaisIntervaloProjection totaisPorIntervalo(Long usuarioId, OffsetDateTime inicio, OffsetDateTime fim);

}
