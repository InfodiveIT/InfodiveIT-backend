package br.com.infodive.infodive_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "solucoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solucao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String titulo;

    private String icone;

    @Column(name = "subtitulo_curto")
    private String subtituloCurto;

    @Column(name = "descricao_curta")
    private String descricaoCurta;

    @Column(name = "recurso_chave_1")
    private String recursoChave1;

    @Column(name = "recurso_chave_2")
    private String recursoChave2;

    @Column(name = "recurso_chave_3")
    private String recursoChave3;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<FeatureItem> features;

    @Column(name = "imagem_url")
    private String imagemUrl;

    @Column(name = "fabricantes_titulo")
    private String fabricantesTitulo;

    @Column(name = "fabricantes_descricao")
    private String fabricantesDescricao;

    @Builder.Default
    private int ordem = 0;

    @Builder.Default
    private boolean ativo = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "solucoes_fabricantes",
            joinColumns = @JoinColumn(name = "solucao_id"),
            inverseJoinColumns = @JoinColumn(name = "fabricante_id")
    )
    @Builder.Default
    private List<Fabricante> fabricantes = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
