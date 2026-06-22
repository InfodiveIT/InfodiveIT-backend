package br.com.infodive.infodive_api.dto.response;

import java.util.UUID;

/** Versão curta para listagem — espelha ProdutoResumoDTO do frontend. */
public record ProdutoResumoResponse(
        UUID id,
        String nome,
        String slug,
        String subcategoria,
        String descricaoCurta,
        boolean destaque,
        String categoriaSlug,
        String fabricanteSlug
) {}
