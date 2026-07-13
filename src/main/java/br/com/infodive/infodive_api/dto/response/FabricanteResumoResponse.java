package br.com.infodive.infodive_api.dto.response;

import java.util.UUID;

public record FabricanteResumoResponse(
        UUID id,
        String nome,
        String slug,
        String logoUrl
) {}
