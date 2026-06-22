package br.com.infodive.infodive_api.mapper;

import br.com.infodive.infodive_api.dto.response.ProdutoDetalheResponse;
import br.com.infodive.infodive_api.dto.response.ProdutoResumoResponse;
import br.com.infodive.infodive_api.entity.Produto;
import br.com.infodive.infodive_api.entity.Servico;
import br.com.infodive.infodive_api.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProdutoMapper {

    private final ObjectMapper objectMapper;

    public ProdutoResumoResponse toResumoResponse(Produto entity) {
        return new ProdutoResumoResponse(
                entity.getId(),
                entity.getNome(),
                entity.getSlug(),
                entity.getSubcategoria(),
                entity.getDescricaoCurta(),
                entity.isDestaque(),
                entity.getSolucao() != null ? entity.getSolucao().getSlug() : null,
                entity.getFabricante() != null ? entity.getFabricante().getSlug() : null
        );
    }

    public ProdutoDetalheResponse toDetalheResponse(Produto entity) {
        List<UUID> servicoIds = entity.getServicos() == null
                ? List.of()
                : entity.getServicos().stream().map(Servico::getId).toList();
        return new ProdutoDetalheResponse(
                entity.getId(),
                entity.getNome(),
                entity.getSlug(),
                entity.getSubcategoria(),
                entity.getDescricaoCurta(),
                entity.getDescricaoCompleta(),
                toJson(entity.getCasosDeUso()),
                toJson(entity.getDiferenciais()),
                entity.isDestaque(),
                entity.isAtivo(),
                entity.getSolucao() != null ? entity.getSolucao().getId() : null,
                entity.getSolucao() != null ? entity.getSolucao().getSlug() : null,
                entity.getFabricante() != null ? entity.getFabricante().getId() : null,
                entity.getFabricante() != null ? entity.getFabricante().getSlug() : null,
                servicoIds,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /** Serializa a lista jsonb para String JSON (o contrato do frontend tipa estes campos como string). */
    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Falha ao serializar campo JSON do produto");
        }
    }
}
