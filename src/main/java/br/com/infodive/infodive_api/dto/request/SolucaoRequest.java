package br.com.infodive.infodive_api.dto.request;

import br.com.infodive.infodive_api.entity.FeatureItem;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

public record SolucaoRequest(
        @NotBlank String slug,
        @NotBlank String titulo,
        String icone,
        String subtituloCurto,
        String descricaoCurta,
        String overview,
        List<FeatureItem> features,
        String imagemUrl,
        String fabricantesTitulo,
        String fabricantesDescricao,
        int ordem,
        UUID categoriaId,
        List<UUID> fabricanteIds
) {}
