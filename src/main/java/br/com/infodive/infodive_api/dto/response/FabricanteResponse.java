package br.com.infodive.infodive_api.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Espelha o FabricanteDTO esperado pelo frontend (src/lib/api.ts).
 * {@code categoriaIds} vem da junção N:N solucoes_fabricantes (preenchido na Fase 3).
 */
public record FabricanteResponse(
        UUID id,
        String nome,
        String slug,
        String descricao,
        String descricaoCurta,
        String logoUrl,
        String siteOficial,
        boolean destaque,
        int ordem,
        boolean ativo,
        List<UUID> categoriaIds,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
