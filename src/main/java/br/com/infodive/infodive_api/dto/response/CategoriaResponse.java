package br.com.infodive.infodive_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoriaResponse(
        UUID id,
        String nome,
        String slug,
        int ordem,
        boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
