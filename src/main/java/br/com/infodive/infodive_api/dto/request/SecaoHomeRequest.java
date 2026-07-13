package br.com.infodive.infodive_api.dto.request;

public record SecaoHomeRequest(
        String eyebrow,
        String headline,
        String headlineDestaque,
        String subtitulo,
        String boxTitulo,
        String boxDescricao
) {}
