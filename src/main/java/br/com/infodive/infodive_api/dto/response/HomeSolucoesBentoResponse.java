package br.com.infodive.infodive_api.dto.response;

import java.util.UUID;

public record HomeSolucoesBentoResponse(
        UUID id,
        String nome,
        String descricao,
        String icone,
        String imagemIaUrl,
        String textoCarrossel,
        int ordem
) {}
