package br.com.infodive.infodive_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PoliticaRequest(
        @NotBlank(message = "Slug é obrigatório") String slug,
        @NotBlank(message = "Título é obrigatório") String titulo,
        String subtitulo,
        @NotBlank(message = "Conteúdo é obrigatório") String conteudo,
        String ultimaAtualizacao,
        Boolean ativo
) {}
