package br.com.infodive.infodive_api.dto.request;

public record CtaRequest(
        String titulo,
        String subtitulo,
        String ctaTexto,
        String tipoAcao
) {}
