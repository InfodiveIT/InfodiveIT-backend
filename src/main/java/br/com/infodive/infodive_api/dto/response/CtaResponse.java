package br.com.infodive.infodive_api.dto.response;

import java.util.UUID;

public record CtaResponse(
        UUID id,
        String pagina,
        String titulo,
        String subtitulo,
        String ctaTexto,
        String tipoAcao
) {}

