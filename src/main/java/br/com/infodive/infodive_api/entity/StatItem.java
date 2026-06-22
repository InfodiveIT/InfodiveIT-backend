package br.com.infodive.infodive_api.entity;

/** Item do JSONB {@code stats} de SobreNumeros. */
public record StatItem(String prefixo, Integer valor, Integer valorInicial, String sufixo, String label, String coluna) {}
