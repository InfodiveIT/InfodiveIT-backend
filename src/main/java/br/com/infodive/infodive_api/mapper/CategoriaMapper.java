package br.com.infodive.infodive_api.mapper;

import br.com.infodive.infodive_api.dto.request.CategoriaRequest;
import br.com.infodive.infodive_api.dto.response.CategoriaResponse;
import br.com.infodive.infodive_api.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public CategoriaResponse toResponse(Categoria entity) {
        if (entity == null) return null;
        return new CategoriaResponse(
                entity.getId(),
                entity.getNome(),
                entity.getSlug(),
                entity.getOrdem(),
                entity.isAtivo(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public Categoria toEntity(CategoriaRequest request) {
        if (request == null) return null;
        return Categoria.builder()
                .nome(request.nome())
                .slug(request.slug())
                .ordem(request.ordem())
                .build();
    }

    public void updateEntity(Categoria entity, CategoriaRequest request) {
        if (entity == null || request == null) return;
        entity.setNome(request.nome());
        entity.setOrdem(request.ordem());
    }
}
