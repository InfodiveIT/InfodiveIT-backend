package br.com.infodive.infodive_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "secoes_home")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecaoHome {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String secao;

    private String eyebrow;

    private String headline;

    @Column(name = "headline_destaque")
    private String headlineDestaque;

    private String subtitulo;

    @Column(name = "box_titulo")
    private String boxTitulo;

    @Column(name = "box_descricao", columnDefinition = "TEXT")
    private String boxDescricao;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
