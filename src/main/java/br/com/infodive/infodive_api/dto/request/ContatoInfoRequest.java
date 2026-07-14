package br.com.infodive.infodive_api.dto.request;

import java.util.List;

public record ContatoInfoRequest(
        String eyebrow,
        String headline,
        String headlineDestaque,
        String subtitulo,
        String email,
        String telefone,
        String endereco,
        String horarioComercial,
        String horarioNoc,
        String cardTitulo,
        String cardDescricao,
        List<String> cardBullets,
        String cardCtaTexto,
        String cardStatus
) {}
