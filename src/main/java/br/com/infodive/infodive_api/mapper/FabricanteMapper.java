package br.com.infodive.infodive_api.mapper;

import br.com.infodive.infodive_api.dto.request.FabricanteRequest;
import br.com.infodive.infodive_api.dto.response.FabricanteResponse;
import br.com.infodive.infodive_api.entity.Fabricante;
import br.com.infodive.infodive_api.entity.Solucao;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FabricanteMapper {

    public FabricanteResponse toResponse(Fabricante entity) {
        List<UUID> categoriaIds = entity.getSolucoes() == null
                ? List.of()
                : entity.getSolucoes().stream().map(Solucao::getId).toList();
        return new FabricanteResponse(
                entity.getId(),
                entity.getNome(),
                entity.getSlug(),
                entity.getDescricao(),
                entity.getDescricaoCurta(),
                entity.getLogoUrl(),
                entity.getSiteOficial(),
                entity.isDestaque(),
                entity.getOrdem(),
                entity.isAtivo(),
                categoriaIds,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public Fabricante toEntity(FabricanteRequest request) {
        return Fabricante.builder()
                .slug(request.slug())
                .nome(request.nome())
                .descricao(request.descricao())
                .descricaoCurta(request.descricaoCurta())
                .logoUrl(request.logoUrl())
                .siteOficial(request.siteOficial())
                .destaque(request.destaque())
                .ordem(request.ordem())
                .build();
    }

    /**
     * Atualiza a entidade a partir do request. O slug é imutável após a criação (regra do CLAUDE.md),
     * portanto não é alterado aqui.
     */
    public void updateEntity(Fabricante entity, FabricanteRequest request) {
        entity.setNome(request.nome());
        entity.setDescricao(request.descricao());
        entity.setDescricaoCurta(request.descricaoCurta());
        entity.setLogoUrl(request.logoUrl());
        entity.setSiteOficial(request.siteOficial());
        entity.setDestaque(request.destaque());
        entity.setOrdem(request.ordem());
    }
}
