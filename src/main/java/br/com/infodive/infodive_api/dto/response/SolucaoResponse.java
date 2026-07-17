package br.com.infodive.infodive_api.dto.response;

import br.com.infodive.infodive_api.entity.FeatureItem;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Espelha o SolucaoDTO esperado pelo frontend (src/lib/api.ts) e react-admin.
 * Mapeamentos: {@code nome} / {@code titulo} ← titulo, {@code descricaoCompleta} / {@code overview} ← overview.
 */
public record SolucaoResponse(
        UUID id,
        String nome,
        String titulo,
        String slug,
        String icone,
        String subtituloCurto,
        String descricaoCurta,
        String descricaoCompleta,
        String overview,
        List<FeatureItem> features,
        String imagemUrl,
        String fabricantesTitulo,
        String fabricantesDescricao,
        List<FabricanteResumoResponse> fabricantes,
        List<UUID> fabricanteIds,
        int ordem,
        boolean ativo,
        UUID categoriaId,
        String categoriaNome,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

