package br.com.infodive.infodive_api.dto.response;

import java.util.UUID;

/** Retorno do POST /leads público — espelha { id, message } esperado pelo frontend. */
public record LeadCreatedResponse(UUID id, String message) {}
