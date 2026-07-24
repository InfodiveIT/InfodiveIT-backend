package br.com.infodive.infodive_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record LogAuditoriaResponse(
        UUID id,
        String usuarioEmail,
        String usuarioNome,
        String acao,
        String recurso,
        String recursoId,
        String detalhes,
        LocalDateTime criadoEm
) {}
