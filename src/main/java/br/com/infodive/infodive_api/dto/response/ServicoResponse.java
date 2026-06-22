package br.com.infodive.infodive_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/** Espelha o ServicoDTO do frontend (src/lib/api.ts). */
public record ServicoResponse(
        UUID id,
        String nome,
        String slug,
        String descricao,
        String icone,
        int ordem,
        boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
