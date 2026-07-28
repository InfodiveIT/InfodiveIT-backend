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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "politicas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Politica {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug; // politica-de-privacidade, termos-de-uso, politica-de-cookies

    @Column(nullable = false)
    private String titulo;

    @Column(name = "subtitulo")
    private String subtitulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String conteudo;

    @Column(name = "ultima_atualizacao")
    private String ultimaAtualizacao;

    @Builder.Default
    private boolean ativo = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
