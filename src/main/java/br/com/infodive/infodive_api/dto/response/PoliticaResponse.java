package br.com.infodive.infodive_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record PoliticaResponse(
        UUID id,
        String slug,
        String titulo,
        String subtitulo,
        String conteudo,
        String ultimaAtualizacao,
        boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
