package br.com.infodive.infodive_api.entity;

/** Item do JSONB {@code fotos} de SobreCultura. */
public record FotoItem(String imagemUrl, String alt, int ordem) {}
