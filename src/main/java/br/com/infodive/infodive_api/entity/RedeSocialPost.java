package br.com.infodive.infodive_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
    name = "social_posts",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_social_posts_rede_external_id", columnNames = {"rede", "external_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedeSocialPost {

    public enum Rede {
        INSTAGRAM,
        LINKEDIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rede rede;

    @Column(name = "external_id", nullable = false)
    private String externalId;

    @Column(name = "texto_legenda", columnDefinition = "TEXT")
    private String textoLegenda;

    @Column(name = "imagem_url", columnDefinition = "TEXT")
    private String imagemUrl;

    @Column(name = "permalink_url", columnDefinition = "TEXT")
    private String permalinkUrl;

    @Builder.Default
    @Column(name = "likes_count")
    private Integer likesCount = 0;

    @Builder.Default
    @Column(name = "comments_count")
    private Integer commentsCount = 0;

    @Column(name = "publicado_em")
    private LocalDateTime publicadoEm;

    @Builder.Default
    private boolean ativo = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
