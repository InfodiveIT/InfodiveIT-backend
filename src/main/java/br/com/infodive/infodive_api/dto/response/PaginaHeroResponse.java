package br.com.infodive.infodive_api.dto.response;

import java.util.UUID;

public record PaginaHeroResponse(
        UUID id,
        String pagina,
        String eyebrow,
        String headline,
        String headlineDestaque,
        String subtitulo,
        String tagline
) {}

