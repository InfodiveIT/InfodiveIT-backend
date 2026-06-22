package br.com.infodive.infodive_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "home_trust_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeTrustStats {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String eyebrow;

    private String prefixo;

    @Builder.Default
    private int valor = 0;

    @Column(name = "valor_inicial")
    @Builder.Default
    private int valorInicial = 0;

    private String sufixo;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Builder.Default
    private int ordem = 0;
}
