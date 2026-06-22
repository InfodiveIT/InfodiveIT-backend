package br.com.infodive.infodive_api.entity;

/** Item do JSONB {@code marcos} de SobreTimeline. */
public record MarcoItem(String ano, String titulo, String descricao, boolean destaque, int ordem) {}
