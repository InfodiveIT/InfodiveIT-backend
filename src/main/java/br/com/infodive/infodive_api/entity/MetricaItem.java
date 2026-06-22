package br.com.infodive.infodive_api.entity;

/** Item do JSONB {@code metricas} de ServicosMetodologia. */
public record MetricaItem(String prefixo, Integer valor, String sufixo, String label) {}
