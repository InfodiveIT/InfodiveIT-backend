package br.com.infodive.infodive_api.mapper;

import br.com.infodive.infodive_api.dto.request.SolucaoRequest;
import br.com.infodive.infodive_api.dto.response.FabricanteResumoResponse;
import br.com.infodive.infodive_api.dto.response.SolucaoResponse;
import br.com.infodive.infodive_api.entity.Fabricante;
import br.com.infodive.infodive_api.entity.Solucao;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class SolucaoMapper {

    public SolucaoResponse toResponse(Solucao entity) {
        List<FabricanteResumoResponse> fabricantes = entity.getFabricantes() == null
                ? List.of()
                : entity.getFabricantes().stream()
                        .map(f -> new FabricanteResumoResponse(f.getId(), f.getNome(), f.getSlug(), f.getLogoUrl()))
                        .toList();
        List<UUID> fabricanteIds = entity.getFabricantes() == null
                ? List.of()
                : entity.getFabricantes().stream().map(Fabricante::getId).toList();
        UUID categoriaId = entity.getCategoria() != null ? entity.getCategoria().getId() : null;
        String categoriaNome = entity.getCategoria() != null ? entity.getCategoria().getNome() : null;

        List<String> recursosChave = java.util.stream.Stream.of(
                        entity.getRecursoChave1(), entity.getRecursoChave2(), entity.getRecursoChave3())
                .filter(r -> r != null && !r.isBlank())
                .toList();

        return new SolucaoResponse(
                entity.getId(),
                entity.getTitulo(),
                entity.getTitulo(),
                entity.getSlug(),
                entity.getIcone(),
                entity.getSubtituloCurto(),
                entity.getDescricaoCurta(),
                entity.getRecursoChave1(),
                entity.getRecursoChave2(),
                entity.getRecursoChave3(),
                recursosChave,
                entity.getOverview(),
                entity.getOverview(),
                entity.getFeatures(),
                entity.getImagemUrl(),
                entity.getFabricantesTitulo(),
                entity.getFabricantesDescricao(),
                fabricantes,
                fabricanteIds,
                entity.getOrdem(),
                entity.isAtivo(),
                categoriaId,
                categoriaNome,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public Solucao toEntity(SolucaoRequest request) {
        return Solucao.builder()
                .slug(request.slug())
                .titulo(request.titulo())
                .icone(request.icone())
                .subtituloCurto(request.subtituloCurto())
                .descricaoCurta(request.descricaoCurta())
                .recursoChave1(request.recursoChave1())
                .recursoChave2(request.recursoChave2())
                .recursoChave3(request.recursoChave3())
                .overview(request.overview())
                .features(request.features())
                .imagemUrl(request.imagemUrl())
                .fabricantesTitulo(request.fabricantesTitulo())
                .fabricantesDescricao(request.fabricantesDescricao())
                .ordem(request.ordem())
                .ativo(request.ativo())
                .build();
    }

    /**
     * Atualiza os campos escalares. O slug é imutável; as associações de fabricantes
     * são resolvidas no service (precisa do FabricanteRepository).
     */
    public void updateEntity(Solucao entity, SolucaoRequest request) {
        entity.setTitulo(request.titulo());
        entity.setIcone(request.icone());
        entity.setSubtituloCurto(request.subtituloCurto());
        entity.setDescricaoCurta(request.descricaoCurta());
        entity.setRecursoChave1(request.recursoChave1());
        entity.setRecursoChave2(request.recursoChave2());
        entity.setRecursoChave3(request.recursoChave3());
        entity.setOverview(request.overview());
        entity.setFeatures(request.features());
        entity.setImagemUrl(request.imagemUrl());
        entity.setFabricantesTitulo(request.fabricantesTitulo());
        entity.setFabricantesDescricao(request.fabricantesDescricao());
        entity.setOrdem(request.ordem());
        entity.setAtivo(request.ativo());
    }
}
