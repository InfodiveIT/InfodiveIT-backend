package br.com.infodive.infodive_api.dto.response;

public record PaginaHeroResponse(
        String pagina,
        String eyebrow,
        String headline,
        String subtitulo,
        String tagline
) {}
