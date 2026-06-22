package br.com.infodive.infodive_api.dto.response;

import java.util.UUID;

public record HomeProblemasResponse(
        UUID id,
        String titulo,
        String descricao,
        String solucaoIndicada,
        String href,
        int ordem
) {}
