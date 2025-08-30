package com.github.devopMarkz.api_liq_rota.domain.service;

import com.github.devopMarkz.api_liq_rota.api.dto.frete.FreteLoteRequest;
import com.github.devopMarkz.api_liq_rota.api.dto.frete.FreteLoteResponse;
import com.github.devopMarkz.api_liq_rota.api.dto.frete.FreteRequest;
import com.github.devopMarkz.api_liq_rota.api.dto.frete.FreteResponse;
import com.github.devopMarkz.api_liq_rota.api.dto.relatorio.TotaisResponse;
import com.github.devopMarkz.api_liq_rota.api.dto.viagem.ViagemResponseDTO;
import com.github.devopMarkz.api_liq_rota.domain.model.Usuario;
import com.github.devopMarkz.api_liq_rota.domain.model.Viagem;
import com.github.devopMarkz.api_liq_rota.domain.repository.ViagemRepository;
import com.github.devopMarkz.api_liq_rota.infraestructure.mapper.ViagemMapper;
import com.github.devopMarkz.api_liq_rota.infraestructure.security.UsuarioAutenticadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculoFreteService {

    private final ViagemRepository viagemRepositorio;
    private final ViagemMapper viagemMapper;

    /* ===================== CÁLCULO (sem persistir) ===================== */

    public FreteResponse calcular(FreteRequest req) {
        return calcularInterno(req);
    }

    public FreteLoteResponse calcularLote(FreteLoteRequest lote) {
        List<FreteResponse> itens = new ArrayList<>();
        for (FreteRequest r : lote.getViagens()) {
            itens.add(calcularInterno(r));
        }
        return FreteLoteResponse.builder()
                .itens(itens)
                .totais(somarTotais(itens))
                .build();
    }

    /* ===================== PERSISTÊNCIA (retornando DTO) ===================== */

    @Transactional
    public ViagemResponseDTO criarViagem(FreteRequest req) {
        Usuario user = UsuarioAutenticadoService.getUsuarioAutenticado();
        FreteResponse calc = calcularInterno(req);

        Viagem v = mapearParaEntidade(req, calc);
        v.setUsuario(user);

        Viagem salvo = viagemRepositorio.save(v);
        return viagemMapper.toResponse(salvo);
    }

    @Transactional
    public List<ViagemResponseDTO> criarViagensEmLote(FreteLoteRequest lote) {
        List<ViagemResponseDTO> saida = new ArrayList<>();
        for (FreteRequest req : lote.getViagens()) {
            saida.add(criarViagem(req)); // reaproveita cálculo + save + mapping
        }
        return saida;
    }

    public ViagemResponseDTO obterViagem(Long id) {
        Viagem entity = obterViagemEntity(id);
        return viagemMapper.toResponse(entity);
    }

    public Page<ViagemResponseDTO> listarViagens(String origem, String destino, Pageable pageable) {
        Usuario user = UsuarioAutenticadoService.getUsuarioAutenticado();
        String o = origem == null ? "" : origem;
        String d = destino == null ? "" : destino;
        Page<Viagem> page = viagemRepositorio
                .findByUsuarioIdAndOrigemContainingIgnoreCaseAndDestinoContainingIgnoreCase(
                        user.getId(), o, d, pageable);
        return viagemMapper.toResponsePage(page);
    }

    @Transactional
    public ViagemResponseDTO atualizarViagem(Long id, FreteRequest req) {
        Viagem v = obterViagemEntity(id); // garante propriedade
        FreteResponse calc = calcularInterno(req);

        v.setOrigem(req.getOrigem());
        v.setDestino(req.getDestino());
        v.setDistanciaKm(req.getDistanciaKm());
        v.setConsumoKmPorLitro(req.getConsumoKmPorLitro());
        v.setPrecoLitro(req.getPrecoLitro());
        v.setGastosAdicionais(req.getGastosAdicionais());
        v.setValorFrete(req.getValorFrete());
        v.setIdaEVolta(Boolean.TRUE.equals(req.getIdaEVolta()));
        v.setCustoCombustivel(calc.getCustoCombustivel());
        v.setValorLiquido(calc.getValorLiquido());

        Viagem atualizado = viagemRepositorio.save(v);
        return viagemMapper.toResponse(atualizado);
    }

    @Transactional
    public void removerViagem(Long id) {
        Viagem v = obterViagemEntity(id); // garante propriedade
        viagemRepositorio.delete(v);
    }

    /* ===================== HELPERS ===================== */

    private Viagem obterViagemEntity(Long id) {
        Usuario user = UsuarioAutenticadoService.getUsuarioAutenticado();
        return viagemRepositorio.findByIdAndUsuarioId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Viagem não encontrada"));
    }

    private FreteResponse calcularInterno(FreteRequest req) {
        BigDecimal distanciaEfetiva = Boolean.TRUE.equals(req.getIdaEVolta())
                ? req.getDistanciaKm().multiply(BigDecimal.valueOf(2))
                : req.getDistanciaKm();

        BigDecimal litrosEq = distanciaEfetiva
                .divide(req.getConsumoKmPorLitro(), 6, RoundingMode.HALF_UP);
        BigDecimal custoComb = litrosEq.multiply(req.getPrecoLitro());

        BigDecimal gastoTotal = custoComb.add(req.getGastosAdicionais());
        BigDecimal liquido = req.getValorFrete().subtract(gastoTotal);

        BigDecimal ganhoPorKm = null;
        if (distanciaEfetiva.compareTo(BigDecimal.ZERO) > 0) {
            ganhoPorKm = liquido.divide(distanciaEfetiva, 6, RoundingMode.HALF_UP)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return FreteResponse.builder()
                .origem(req.getOrigem())
                .destino(req.getDestino())
                .distanciaConsideradaKm(distanciaEfetiva.setScale(2, RoundingMode.HALF_UP))
                .idaEVolta(Boolean.TRUE.equals(req.getIdaEVolta()))
                .custoCombustivel(custoComb.setScale(2, RoundingMode.HALF_UP))
                .gastosAdicionais(req.getGastosAdicionais().setScale(2, RoundingMode.HALF_UP))
                .gastoTotal(gastoTotal.setScale(2, RoundingMode.HALF_UP))
                .valorFrete(req.getValorFrete().setScale(2, RoundingMode.HALF_UP))
                .valorLiquido(liquido.setScale(2, RoundingMode.HALF_UP))
                .ganhoPorKm(ganhoPorKm)
                .build();
    }

    private TotaisResponse somarTotais(List<FreteResponse> itens) {
        BigDecimal distTot = BigDecimal.ZERO, combTot = BigDecimal.ZERO, gadTot = BigDecimal.ZERO,
                gtoTot  = BigDecimal.ZERO, freteTot = BigDecimal.ZERO, liqTot = BigDecimal.ZERO;

        for (FreteResponse r : itens) {
            distTot  = distTot.add(r.getDistanciaConsideradaKm());
            combTot  = combTot.add(r.getCustoCombustivel());
            gadTot   = gadTot.add(r.getGastosAdicionais());
            gtoTot   = gtoTot.add(r.getGastoTotal());
            freteTot = freteTot.add(r.getValorFrete());
            liqTot   = liqTot.add(r.getValorLiquido());
        }

        BigDecimal ganhoKmTot = null;
        if (distTot.compareTo(BigDecimal.ZERO) > 0) {
            ganhoKmTot = liqTot.divide(distTot, 6, RoundingMode.HALF_UP)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return TotaisResponse.builder()
                .totalDistanciaConsideradaKm(distTot.setScale(2, RoundingMode.HALF_UP))
                .totalCustoCombustivel(combTot.setScale(2, RoundingMode.HALF_UP))
                .totalGastosAdicionais(gadTot.setScale(2, RoundingMode.HALF_UP))
                .totalGastoTotal(gtoTot.setScale(2, RoundingMode.HALF_UP))
                .totalValorFrete(freteTot.setScale(2, RoundingMode.HALF_UP))
                .totalValorLiquido(liqTot.setScale(2, RoundingMode.HALF_UP))
                .ganhoPorKmTotal(ganhoKmTot)
                .build();
    }

    private Viagem mapearParaEntidade(FreteRequest req, FreteResponse calc) {
        return Viagem.builder()
                .origem(req.getOrigem())
                .destino(req.getDestino())
                .distanciaKm(req.getDistanciaKm())
                .consumoKmPorLitro(req.getConsumoKmPorLitro())
                .precoLitro(req.getPrecoLitro())
                .gastosAdicionais(req.getGastosAdicionais())
                .valorFrete(req.getValorFrete())
                .idaEVolta(Boolean.TRUE.equals(req.getIdaEVolta()))
                .custoCombustivel(calc.getCustoCombustivel())
                .valorLiquido(calc.getValorLiquido())
                .build();
    }
}
