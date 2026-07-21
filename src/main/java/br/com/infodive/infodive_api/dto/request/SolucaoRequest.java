package br.com.infodive.infodive_api.dto.request;

import br.com.infodive.infodive_api.entity.FeatureItem;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public record SolucaoRequest(
        String slug,
        @JsonProperty("nome") @JsonAlias({"nome", "titulo"}) String nome,
        @JsonProperty("titulo") @JsonAlias({"nome", "titulo"}) String titulo,
        String icone,
        String subtituloCurto,
        String descricaoCurta,
        String recursoChave1,
        String recursoChave2,
        String recursoChave3,
        @JsonProperty("descricaoCompleta") @JsonAlias({"descricaoCompleta", "overview"}) String descricaoCompleta,
        @JsonProperty("overview") @JsonAlias({"descricaoCompleta", "overview"}) String overview,
        List<FeatureItem> features,
        String imagemUrl,
        String fabricantesTitulo,
        String fabricantesDescricao,
        int ordem,
        boolean ativo,
        UUID categoriaId,
        List<UUID> fabricanteIds
) {
    public String getEffectiveTitulo() {
        if (titulo != null && !titulo.isBlank()) return titulo;
        if (nome != null && !nome.isBlank()) return nome;
        return null;
    }

    public String getEffectiveOverview() {
        if (overview != null && !overview.isBlank()) return overview;
        if (descricaoCompleta != null && !descricaoCompleta.isBlank()) return descricaoCompleta;
        return null;
    }
}

