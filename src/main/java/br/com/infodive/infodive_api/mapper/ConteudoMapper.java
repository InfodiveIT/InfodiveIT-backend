package br.com.infodive.infodive_api.mapper;

import br.com.infodive.infodive_api.dto.response.ConteudoResponse;
import br.com.infodive.infodive_api.entity.Conteudo;
import br.com.infodive.infodive_api.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConteudoMapper {

    private final ObjectMapper objectMapper;

    public ConteudoResponse toResponse(Conteudo entity) {
        return new ConteudoResponse(
                entity.getId(),
                entity.getTitulo(),
                entity.getSlug(),
                entity.getTipo(),
                entity.getOrigem(),
                entity.getDescricao(),
                toJson(entity.getConteudo()),
                entity.getUrlExterna(),
                entity.getSocialPostId(),
                entity.getPublicadoEm(),
                entity.isAtivo(),
                entity.getCategoria() != null ? entity.getCategoria().getId() : null,
                entity.getFabricante() != null ? entity.getFabricante().getId() : null,
                entity.getProduto() != null ? entity.getProduto().getId() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Falha ao serializar o conteúdo");
        }
    }
}
