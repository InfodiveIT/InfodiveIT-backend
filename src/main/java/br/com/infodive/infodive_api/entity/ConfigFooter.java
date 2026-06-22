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
@Table(name = "config_footer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigFooter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "descricao_empresa", columnDefinition = "TEXT")
    private String descricaoEmpresa;

    @Column(name = "badge_noc")
    private String badgeNoc;

    @Column(name = "badge_cloud")
    private String badgeCloud;

    @Column(name = "nome_legal")
    private String nomeLegal;

    @Column(name = "url_linkedin")
    private String urlLinkedin;

    @Column(name = "url_instagram")
    private String urlInstagram;

    @Column(name = "url_facebook")
    private String urlFacebook;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
