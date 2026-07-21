package br.com.infodive.infodive_api.mapper;

import br.com.infodive.infodive_api.dto.request.SolucaoRequest;
import br.com.infodive.infodive_api.dto.response.FabricanteResumoResponse;
import br.com.infodive.infodive_api.dto.response.SolucaoResponse;
import br.com.infodive.infodive_api.entity.FeatureItem;
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

        List<String> recursosChave = resolveRecursosChave(
                entity.getRecursoChave1(),
                entity.getRecursoChave2(),
                entity.getRecursoChave3(),
                entity.getFeatures()
        );

        return new SolucaoResponse(
                entity.getId(),
                entity.getTitulo(),
                entity.getTitulo(),
                entity.getSlug(),
                entity.getIcone(),
                entity.getSubtituloCurto(),
                entity.getDescricaoCurta(),
                recursoChaveOrFeature(entity.getRecursoChave1(), entity.getFeatures(), 0),
                recursoChaveOrFeature(entity.getRecursoChave2(), entity.getFeatures(), 1),
                recursoChaveOrFeature(entity.getRecursoChave3(), entity.getFeatures(), 2),
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
        String effectiveTitulo = request.getEffectiveTitulo();
        String effectiveOverview = request.getEffectiveOverview();

        List<String> recursosChave = resolveRecursosChave(
                request.recursoChave1(),
                request.recursoChave2(),
                request.recursoChave3(),
                request.features()
        );

        return Solucao.builder()
                .slug(request.slug())
                .titulo(effectiveTitulo)
                .icone(request.icone())
                .subtituloCurto(request.subtituloCurto())
                .descricaoCurta(request.descricaoCurta())
                .recursoChave1(recursosChave.size() > 0 ? recursosChave.get(0) : null)
                .recursoChave2(recursosChave.size() > 1 ? recursosChave.get(1) : null)
                .recursoChave3(recursosChave.size() > 2 ? recursosChave.get(2) : null)
                .overview(effectiveOverview)
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
        String effectiveTitulo = request.getEffectiveTitulo();
        String effectiveOverview = request.getEffectiveOverview();

        List<String> recursosChave = resolveRecursosChave(
                request.recursoChave1(),
                request.recursoChave2(),
                request.recursoChave3(),
                request.features()
        );

        if (effectiveTitulo != null && !effectiveTitulo.isBlank()) {
            entity.setTitulo(effectiveTitulo);
        }
        if (effectiveOverview != null) {
            entity.setOverview(effectiveOverview);
        }
        if (request.slug() != null && !request.slug().isBlank()) {
            entity.setSlug(request.slug());
        }
        entity.setIcone(request.icone());
        entity.setSubtituloCurto(request.subtituloCurto());
        entity.setDescricaoCurta(request.descricaoCurta());
        entity.setRecursoChave1(recursosChave.size() > 0 ? recursosChave.get(0) : null);
        entity.setRecursoChave2(recursosChave.size() > 1 ? recursosChave.get(1) : null);
        entity.setRecursoChave3(recursosChave.size() > 2 ? recursosChave.get(2) : null);
        entity.setFeatures(request.features());
        entity.setImagemUrl(request.imagemUrl());
        entity.setFabricantesTitulo(request.fabricantesTitulo());
        entity.setFabricantesDescricao(request.fabricantesDescricao());
        entity.setOrdem(request.ordem());
        entity.setAtivo(request.ativo());
    }

    private List<String> resolveRecursosChave(
            String recursoChave1,
            String recursoChave2,
            String recursoChave3,
            List<FeatureItem> features
    ) {
        List<String> recursosChave = java.util.stream.Stream.of(recursoChave1, recursoChave2, recursoChave3)
                .filter(r -> r != null && !r.isBlank())
                .toList();

        if (!recursosChave.isEmpty()) {
            return recursosChave;
        }

        if (features == null) {
            return List.of();
        }

        return features.stream()
                .map(FeatureItem::titulo)
                .filter(titulo -> titulo != null && !titulo.isBlank())
                .limit(3)
                .toList();
    }

    private String recursoChaveOrFeature(String recursoChave, List<FeatureItem> features, int index) {
        if (recursoChave != null && !recursoChave.isBlank()) {
            return recursoChave;
        }
        if (features == null || features.size() <= index || features.get(index) == null) {
            return null;
        }
        String titulo = features.get(index).titulo();
        return titulo != null && !titulo.isBlank() ? titulo : null;
    }
}
