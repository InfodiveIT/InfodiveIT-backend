package br.com.infodive.infodive_api.dto.request;

import br.com.infodive.infodive_api.entity.FeatureItem;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

public record SolucaoRequest(
        @NotBlank String slug,
        @JsonAlias({"nome", "titulo"}) @NotBlank String titulo,
        String icone,
        String subtituloCurto,
        String descricaoCurta,
        String recursoChave1,
        String recursoChave2,
        String recursoChave3,
        @JsonAlias({"descricaoCompleta", "overview"}) String overview,
        List<FeatureItem> features,
        String imagemUrl,
        String fabricantesTitulo,
        String fabricantesDescricao,
        int ordem,
        boolean ativo,
        UUID categoriaId,
        List<UUID> fabricanteIds
) {}

