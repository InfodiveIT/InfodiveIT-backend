package br.com.infodive.infodive_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParceiroTokenResponse(
        UUID id,
        String nomeAgencia,
        String email,
        String token,
        String role,
        LocalDateTime expiraEm,
        boolean ativo,
        boolean expirado,
        String criadoPor,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {}
