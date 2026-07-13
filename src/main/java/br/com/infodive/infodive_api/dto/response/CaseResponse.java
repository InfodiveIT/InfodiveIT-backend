package br.com.infodive.infodive_api.dto.response;

import java.util.UUID;

/** Case de sucesso — usado no carrossel da home. */
public record CaseResponse(
        UUID id,
        String segmento,
        String cliente,
        String titulo,
        String desafio,
        String resultado,
        String metrica,
        String autor,
        String cargo,
        String depoimento,
        String imagemUrl,
        int ordem,
        boolean ativo
) {}
