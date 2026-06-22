package br.com.infodive.infodive_api.dto.response;

public record LoginResponse(
    String token,
    String email,
    String nome
) {}
