package br.com.infodive.infodive_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(
        @NotBlank String nome,
        @NotBlank String slug,
        int ordem
) {}
