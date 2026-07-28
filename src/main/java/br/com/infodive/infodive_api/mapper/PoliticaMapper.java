package br.com.infodive.infodive_api.mapper;

import br.com.infodive.infodive_api.dto.request.PoliticaRequest;
import br.com.infodive.infodive_api.dto.response.PoliticaResponse;
import br.com.infodive.infodive_api.entity.Politica;
import org.springframework.stereotype.Component;

@Component
public class PoliticaMapper {

    public PoliticaResponse toResponse(Politica entity) {
        if (entity == null) return null;
        return new PoliticaResponse(
                entity.getId(),
                entity.getSlug(),
                entity.getTitulo(),
                entity.getSubtitulo(),
                entity.getConteudo(),
                entity.getUltimaAtualizacao(),
                entity.isAtivo(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public Politica toEntity(PoliticaRequest request) {
        if (request == null) return null;
        return Politica.builder()
                .slug(request.slug())
                .titulo(request.titulo())
                .subtitulo(request.subtitulo())
                .conteudo(request.conteudo())
                .ultimaAtualizacao(request.ultimaAtualizacao())
                .ativo(request.ativo() != null ? request.ativo() : true)
                .build();
    }

    public void updateEntity(Politica entity, PoliticaRequest request) {
        if (entity == null || request == null) return;
        entity.setSlug(request.slug());
        entity.setTitulo(request.titulo());
        entity.setSubtitulo(request.subtitulo());
        entity.setConteudo(request.conteudo());
        entity.setUltimaAtualizacao(request.ultimaAtualizacao());
        if (request.ativo() != null) {
            entity.setAtivo(request.ativo());
        }
    }
}
