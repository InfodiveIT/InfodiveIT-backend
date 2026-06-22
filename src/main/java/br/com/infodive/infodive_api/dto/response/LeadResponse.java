package br.com.infodive.infodive_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/** Listagem de leads para o admin (GET protegido). */
public record LeadResponse(
        UUID id,
        String nomeCompleto,
        String email,
        String telefone,
        String empresa,
        String cargo,
        String mensagem,
        boolean consentimentoLgpd,
        UUID produtoInteresseId,
        LocalDateTime criadoEm
) {}
