package br.com.infodive.infodive_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CaseRequest(
        @NotBlank String segmento,
        @NotBlank String cliente,
        @NotBlank String titulo,
        @NotBlank String desafio,
        @NotBlank String resultado,
        @NotBlank String metrica,
        @NotBlank String autor,
        @NotBlank String cargo,
        @NotBlank String depoimento,
        String imagemUrl,
        int ordem,
        boolean ativo
) {}
