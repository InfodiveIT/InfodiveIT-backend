package br.com.infodive.infodive_api.entity;

import java.util.List;

/**
 * Bloco do campo JSONB {@code conteudo} de Conteudo.
 * tipo: paragrafo | subtitulo | lista | citacao. {@code itens} só é usado quando tipo = lista.
 */
public record ConteudoBloco(String tipo, String texto, List<String> itens) {}
