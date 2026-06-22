package br.com.infodive.infodive_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminAutorizadoResponse(
    UUID id,
    String email,
    String nome,
    boolean ativo,
    LocalDateTime criadoEm
) {}
