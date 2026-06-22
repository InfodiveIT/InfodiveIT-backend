package br.com.infodive.infodive_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "contato_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContatoInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String eyebrow;

    private String headline;

    private String subtitulo;

    private String email;

    private String telefone;

    private String endereco;

    @Column(name = "horario_comercial")
    private String horarioComercial;

    @Column(name = "horario_noc")
    private String horarioNoc;

    @Column(name = "card_titulo")
    private String cardTitulo;

    @Column(name = "card_descricao", columnDefinition = "TEXT")
    private String cardDescricao;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "card_bullets", columnDefinition = "jsonb")
    private List<String> cardBullets;

    @Column(name = "card_cta_texto")
    private String cardCtaTexto;

    @Column(name = "card_status")
    private String cardStatus;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
