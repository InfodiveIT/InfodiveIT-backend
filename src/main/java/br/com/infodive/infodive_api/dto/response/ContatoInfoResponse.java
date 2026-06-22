package br.com.infodive.infodive_api.dto.response;

import java.util.List;

public record ContatoInfoResponse(
        String eyebrow,
        String headline,
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
