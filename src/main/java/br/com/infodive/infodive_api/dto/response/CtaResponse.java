package br.com.infodive.infodive_api.dto.response;

public record CtaResponse(
        String pagina,
        String titulo,
        String subtitulo,
        String ctaTexto
) {}
