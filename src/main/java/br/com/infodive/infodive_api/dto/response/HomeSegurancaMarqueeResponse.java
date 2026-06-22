package br.com.infodive.infodive_api.dto.response;

import java.util.UUID;

public record HomeSegurancaMarqueeResponse(
        UUID id,
        String icone,
        String titulo,
        String corpo,
        int ordem
) {}
