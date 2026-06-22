package br.com.infodive.infodive_api.dto.response;

import java.util.UUID;

public record HomeTrustStatsResponse(
        UUID id,
        String eyebrow,
        String prefixo,
        int valor,
        int valorInicial,
        String sufixo,
        String titulo,
        String descricao,
        int ordem
) {}
