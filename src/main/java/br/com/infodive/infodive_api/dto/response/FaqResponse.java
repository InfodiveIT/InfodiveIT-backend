package br.com.infodive.infodive_api.dto.response;

import java.util.UUID;

public record FaqResponse(
        UUID id,
        String pergunta,
        String resposta,
        int ordem
) {}
