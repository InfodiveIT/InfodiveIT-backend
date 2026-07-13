package br.com.infodive.infodive_api.dto.response;

import br.com.infodive.infodive_api.entity.FeatureItem;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Espelha o CategoriaDTO esperado pelo frontend (src/lib/api.ts).
 * No banco a tabela chama-se {@code solucoes}; o frontend consome como "categoria".
 * Mapeamentos: {@code nome} ← titulo, {@code descricaoCompleta} ← overview.
 */
public record SolucaoResponse(
        UUID id,
        String nome,
        String slug,
        String icone,
        String subtituloCurto,
        String descricaoCurta,
        String descricaoCompleta,
        List<FeatureItem> features,
        String imagemUrl,
        String fabricantesTitulo,
        String fabricantesDescricao,
        List<FabricanteResumoResponse> fabricantes,
        int ordem,
        boolean ativo,
        UUID categoriaId,
        String categoriaNome,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
