package com.github.devopMarkz.api_liq_rota.domain.service;

import com.github.devopMarkz.api_liq_rota.api.dto.frete.FreteLoteRequest;
import com.github.devopMarkz.api_liq_rota.api.dto.frete.FreteLoteResponse;
import com.github.devopMarkz.api_liq_rota.api.dto.frete.FreteRequest;
import com.github.devopMarkz.api_liq_rota.api.dto.frete.FreteResponse;
import com.github.devopMarkz.api_liq_rota.api.dto.relatorio.TotaisResponse;
import com.github.devopMarkz.api_liq_rota.api.dto.viagem.ViagemResponseDTO;
import com.github.devopMarkz.api_liq_rota.api.exception.CalculoInvalidoException;
import com.github.devopMarkz.api_liq_rota.api.exception.EntidadeInexistenteException;
import com.github.devopMarkz.api_liq_rota.api.exception.LoteVazioException;
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

    private static final BigDecimal VEL_PADRAO_KMH = BigDecimal.valueOf(60);

    /* ===================== CÁLCULO (sem persistir) ===================== */

    public FreteResponse calcular(FreteRequest req) {
        validarRequest(req, "cálculo");
        return calcularInterno(req);
    }

    public FreteLoteResponse calcularLote(FreteLoteRequest lote) {
        if (lote == null || lote.getViagens() == null || lote.getViagens().isEmpty()) {
            throw new LoteVazioException("A lista de viagens do lote é obrigatória e não pode ser vazia.");
        }
        List<FreteResponse> itens = new ArrayList<>();
        for (int i = 0; i < lote.getViagens().size(); i++) {
            FreteRequest r = lote.getViagens().get(i);
            validarRequest(r, "cálculo do item " + (i + 1));
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
        validarRequest(req, "criação");
        Usuario user = UsuarioAutenticadoService.getUsuarioAutenticado();
        FreteResponse calc = calcularInterno(req);

        Viagem v = mapearParaEntidade(req, calc);
        v.setUsuario(user);

        Viagem salvo = viagemRepositorio.save(v);
        ViagemResponseDTO dto = viagemMapper.toResponse(salvo);
        preencherDuracaoDto(dto);
        return dto;
    }

    @Transactional
    public List<ViagemResponseDTO> criarViagensEmLote(FreteLoteRequest lote) {
        if (lote == null || lote.getViagens() == null || lote.getViagens().isEmpty()) {
            throw new LoteVazioException("A lista de viagens do lote é obrigatória e não pode ser vazia.");
        }
        List<ViagemResponseDTO> saida = new ArrayList<>();
        for (int i = 0; i < lote.getViagens().size(); i++) {
            FreteRequest req = lote.getViagens().get(i);
            validarRequest(req, "criação do item " + (i + 1));
            saida.add(criarViagem(req)); // reaproveita cálculo + save + mapping
        }
        return saida;
    }

    public ViagemResponseDTO obterViagem(Long id) {
        Viagem entity = obterViagemEntity(id);
        ViagemResponseDTO dto = viagemMapper.toResponse(entity);
        preencherDuracaoDto(dto);
        return dto;
    }

    public Page<ViagemResponseDTO> listarViagens(String origem, String destino, Pageable pageable) {
        Usuario user = UsuarioAutenticadoService.getUsuarioAutenticado();
        String o = origem == null ? "" : origem;
        String d = destino == null ? "" : destino;
        Page<Viagem> page = viagemRepositorio
                .findByUsuarioIdAndOrigemContainingIgnoreCaseAndDestinoContainingIgnoreCase(
                        user.getId(), o, d, pageable);

        // Preenche duração em cada item do Page
        return page.map(v -> {
            ViagemResponseDTO dto = viagemMapper.toResponse(v);
            preencherDuracaoDto(dto);
            return dto;
        });
    }

    @Transactional
    public ViagemResponseDTO atualizarViagem(Long id, FreteRequest req) {
        validarRequest(req, "atualização");
        Viagem v = obterViagemEntity(id);
        FreteResponse calc = calcularInterno(req);

        v.setOrigem(req.getOrigem());
        v.setDestino(req.getDestino());
        v.setDistanciaKm(req.getDistanciaKm());
        v.setConsumoKmPorLitro(req.getConsumoKmPorLitro());
        v.setPrecoLitro(req.getPrecoLitro());
        v.setGastosAdicionais(req.getGastosAdicionais());
        v.setValorFrete(calc.getValorFrete()); // usa o valor calculado (pode vir do ganhoPorKmDesejado)
        v.setIdaEVolta(Boolean.TRUE.equals(req.getIdaEVolta()));
        v.setCustoCombustivel(calc.getCustoCombustivel());
        v.setValorLiquido(calc.getValorLiquido());

        Viagem atualizado = viagemRepositorio.save(v);
        ViagemResponseDTO dto = viagemMapper.toResponse(atualizado);
        preencherDuracaoDto(dto);
        return dto;
    }

    @Transactional
    public void removerViagem(Long id) {
        Viagem v = obterViagemEntity(id);
        viagemRepositorio.delete(v);
    }

    /* ===================== HELPERS ===================== */

    private Viagem obterViagemEntity(Long id) {
        Usuario user = UsuarioAutenticadoService.getUsuarioAutenticado();
        return viagemRepositorio.findByIdAndUsuarioId(id, user.getId())
                .orElseThrow(() -> new EntidadeInexistenteException("Viagem não encontrada"));
    }

    private FreteResponse calcularInterno(FreteRequest req) {
        // Distâncias
        BigDecimal distanciaCobranca = req.getDistanciaKm(); // cliente paga só a ida
        BigDecimal distanciaCusto = Boolean.TRUE.equals(req.getIdaEVolta())
                ? req.getDistanciaKm().multiply(BigDecimal.valueOf(2))
                : req.getDistanciaKm(); // motorista roda ida (+volta se marcado)

        // Combustível com base na distância de custo
        BigDecimal litrosEq = (distanciaCusto.compareTo(BigDecimal.ZERO) > 0)
                ? distanciaCusto.divide(req.getConsumoKmPorLitro(), 6, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal custoComb = litrosEq.multiply(req.getPrecoLitro());

        BigDecimal gastoTotal = custoComb.add(req.getGastosAdicionais());

        boolean temFrete   = req.getValorFrete() != null;
        boolean temGanhoKm = req.getGanhoPorKmDesejado() != null;

        BigDecimal valorFrete;
        BigDecimal liquido;
        BigDecimal ganhoPorKm;

        if (temFrete) {
            // MODO A: valor do frete informado (bruto)
            valorFrete = req.getValorFrete();
        } else {
            // MODO B: ganhoPorKmDesejado = PREÇO POR KM (BRUTO) — cliente paga só a ida
            BigDecimal precoPorKmBruto = req.getGanhoPorKmDesejado();
            valorFrete = (distanciaCobranca.compareTo(BigDecimal.ZERO) > 0)
                    ? precoPorKmBruto.multiply(distanciaCobranca)
                    : BigDecimal.ZERO;
        }

        liquido = valorFrete.subtract(gastoTotal);

        // bônus de 40% no valor líquido (se solicitado)
        if (Boolean.TRUE.equals(req.getAplicarBonus40())) {
            liquido = liquido.multiply(BigDecimal.valueOf(1.40));
        }

        // Ganho por km calculado sobre os km rodados (ida+volta se marcado)
        ganhoPorKm = (distanciaCusto.compareTo(BigDecimal.ZERO) > 0)
                ? liquido.divide(distanciaCusto, 6, RoundingMode.HALF_UP)
                : null;

        BigDecimal horas = (distanciaCusto.compareTo(BigDecimal.ZERO) > 0)
                ? distanciaCusto.divide(VEL_PADRAO_KMH, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        String duracaoFmt = formatarDuracao(horas);

        return FreteResponse.builder()
                .origem(req.getOrigem())
                .destino(req.getDestino())
                .distanciaConsideradaKm(distanciaCusto.setScale(2, RoundingMode.HALF_UP))
                .idaEVolta(Boolean.TRUE.equals(req.getIdaEVolta()))
                .custoCombustivel(custoComb.setScale(2, RoundingMode.HALF_UP))
                .gastosAdicionais(req.getGastosAdicionais().setScale(2, RoundingMode.HALF_UP))
                .gastoTotal(gastoTotal.setScale(2, RoundingMode.HALF_UP))
                .valorFrete(valorFrete.setScale(2, RoundingMode.HALF_UP))
                .valorLiquido(liquido.setScale(2, RoundingMode.HALF_UP))
                .ganhoPorKm(ganhoPorKm == null ? null : ganhoPorKm.setScale(2, RoundingMode.HALF_UP))
                .duracaoHoras(horas.setScale(2, RoundingMode.HALF_UP))
                .duracaoFormatada(duracaoFmt)
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
                .valorFrete(calc.getValorFrete())
                .idaEVolta(Boolean.TRUE.equals(req.getIdaEVolta()))
                .custoCombustivel(calc.getCustoCombustivel())
                .valorLiquido(calc.getValorLiquido()) // já com bônus, se aplicado
                .build();
    }

    /* ======== duração helpers ======== */

    private void preencherDuracaoDto(ViagemResponseDTO dto) {
        if (dto == null) return;
        BigDecimal distanciaCusto = Boolean.TRUE.equals(dto.getIdaEVolta())
                ? dto.getDistanciaKm().multiply(BigDecimal.valueOf(2))
                : dto.getDistanciaKm();

        BigDecimal horas = (distanciaCusto.compareTo(BigDecimal.ZERO) > 0)
                ? distanciaCusto.divide(VEL_PADRAO_KMH, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        dto.setDuracaoHoras(horas);
        dto.setDuracaoFormatada(formatarDuracao(horas));
    }

    private String formatarDuracao(BigDecimal horas) {
        // Converte decimal de horas em "Xdias Yh Zmin" ou "Yh Zmin"
        int totalMin = horas.multiply(BigDecimal.valueOf(60))
                .setScale(0, RoundingMode.HALF_UP).intValue();
        int dias = totalMin / (60 * 24);
        int restoMin = totalMin % (60 * 24);
        int h = restoMin / 60;
        int m = restoMin % 60;

        if (dias > 0) {
            return String.format("%d dia%s %d h", dias, dias > 1 ? "s" : "", h);
        }
        if (h > 0 && m > 0) return String.format("%d h %d min", h, m);
        if (h > 0)          return String.format("%d h", h);
        return String.format("%d min", m);
    }

    /* ===================== validações ===================== */

    private void validarRequest(FreteRequest req, String contexto) {
        if (req == null) throw new CalculoInvalidoException("Payload de " + contexto + " é obrigatório.");

        List<String> erros = new ArrayList<>();

        if (isBlank(req.getOrigem()))  erros.add("origem é obrigatória.");
        if (isBlank(req.getDestino())) erros.add("destino é obrigatório.");

        if (req.getDistanciaKm() == null)            erros.add("distanciaKm é obrigatória.");
        else if (req.getDistanciaKm().compareTo(BigDecimal.ZERO) < 0) erros.add("distanciaKm deve ser >= 0.");

        if (req.getConsumoKmPorLitro() == null)      erros.add("consumoKmPorLitro é obrigatório.");
        else if (req.getConsumoKmPorLitro().compareTo(BigDecimal.ZERO) <= 0) erros.add("consumoKmPorLitro deve ser > 0.");

        if (req.getPrecoLitro() == null)             erros.add("precoLitro é obrigatório.");
        else if (req.getPrecoLitro().compareTo(BigDecimal.ZERO) < 0) erros.add("precoLitro deve ser >= 0.");

        if (req.getGastosAdicionais() == null)       erros.add("gastosAdicionais é obrigatório.");
        else if (req.getGastosAdicionais().compareTo(BigDecimal.ZERO) < 0) erros.add("gastosAdicionais deve ser >= 0.");

        // ----- Regra XOR: valorFrete OU ganhoPorKmDesejado -----
        boolean temFrete   = req.getValorFrete() != null;
        boolean temGanhoKm = req.getGanhoPorKmDesejado() != null;

        if (!temFrete && !temGanhoKm) {
            erros.add("Informe 'valorFrete' OU 'ganhoPorKmDesejado'.");
        }
        if (temFrete && temGanhoKm) {
            erros.add("Informe apenas um: 'valorFrete' OU 'ganhoPorKmDesejado'.");
        }

        if (temFrete && req.getValorFrete().compareTo(BigDecimal.ZERO) < 0) {
            erros.add("valorFrete deve ser >= 0.");
        }
        if (temGanhoKm && req.getGanhoPorKmDesejado().compareTo(BigDecimal.ZERO) < 0) {
            erros.add("ganhoPorKmDesejado deve ser >= 0.");
        }
        // -------------------------------------------------------

        if (!erros.isEmpty()) {
            throw new CalculoInvalidoException("Erros na " + contexto + ": " + String.join(" ", erros));
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
