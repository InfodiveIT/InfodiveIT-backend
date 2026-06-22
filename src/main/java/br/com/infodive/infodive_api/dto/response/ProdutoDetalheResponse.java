package br.com.infodive.infodive_api.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Versão completa para /produtos/[slug] — espelha ProdutoDTO do frontend.
 * {@code diferenciais} e {@code casosDeUso} são JSON serializado em String (contrato tipa como string).
 */
public record ProdutoDetalheResponse(
        UUID id,
        String nome,
        String slug,
        String subcategoria,
        String descricaoCurta,
        String descricaoCompleta,
        String casosDeUso,
        String diferenciais,
        boolean destaque,
        boolean ativo,
        UUID categoriaId,
        String categoriaSlug,
        UUID fabricanteId,
        String fabricanteSlug,
        List<UUID> servicoIds,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
