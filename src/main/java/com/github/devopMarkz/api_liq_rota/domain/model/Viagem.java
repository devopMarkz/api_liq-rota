package com.github.devopMarkz.api_liq_rota.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "viagens",
        indexes = {
                @Index(name = "idx_viagens_usuario", columnList = "usuario_id"),
                @Index(name = "idx_viagens_origem_destino", columnList = "origem, destino"),
                @Index(name = "idx_viagens_created_at", columnList = "created_at")
        })
public class Viagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // dono da viagem

    @Column(nullable = false, length = 120)
    private String origem;

    @Column(nullable = false, length = 120)
    private String destino;

    @Column(name = "distancia_km", nullable = false, precision = 12, scale = 3)
    private BigDecimal distanciaKm;

    @Column(name = "consumo_km_por_litro", nullable = false, precision = 12, scale = 6)
    private BigDecimal consumoKmPorLitro;

    @Column(name = "preco_litro", nullable = false, precision = 12, scale = 3)
    private BigDecimal precoLitro;

    @Column(name = "gastos_adicionais", nullable = false, precision = 12, scale = 2)
    private BigDecimal gastosAdicionais;

    @Column(name = "valor_frete", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorFrete;

    @Column(name = "ida_e_volta", nullable = false)
    private Boolean idaEVolta = Boolean.FALSE;

    @Column(name = "custo_combustivel", nullable = false, precision = 12, scale = 2)
    private BigDecimal custoCombustivel;

    @Column(name = "valor_liquido", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorLiquido;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void aoCriar() {
        this.createdAt = OffsetDateTime.now();
    }
}