package br.com.infodive.infodive_api.dto.response;

import java.util.UUID;

public record SecaoHomeResponse(
        UUID id,
        String secao,
        String eyebrow,
        String headline,
        String headlineDestaque,
        String subtitulo,
        String boxTitulo,
        String boxDescricao
) {}

