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
@Table(name = "config_blog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigBlog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "artigos_eyebrow")
    private String artigosEyebrow;

    @Column(name = "artigos_headline")
    private String artigosHeadline;

    @Column(name = "social_eyebrow")
    private String socialEyebrow;

    @Column(name = "social_headline")
    private String socialHeadline;

    @Column(name = "social_descricao", columnDefinition = "TEXT")
    private String socialDescricao;

    @Column(name = "url_instagram")
    private String urlInstagram;

    @Column(name = "url_linkedin")
    private String urlLinkedin;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
