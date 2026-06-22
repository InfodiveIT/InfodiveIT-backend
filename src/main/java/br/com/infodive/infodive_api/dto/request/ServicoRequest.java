package br.com.infodive.infodive_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ServicoRequest(
        @NotBlank String slug,
        @NotBlank String nome,
        String descricao,
        String icone,
        int ordem
) {}
