package br.com.infodive.infodive_api.dto.response;

import java.util.UUID;

public record ServicoResumoResponse(
        UUID id,
        String nome,
        String slug,
        String icone
) {}
