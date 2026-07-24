package br.com.infodive.infodive_api.dto.request;

public record LoginRequest(
    String idToken,
    String email,
    String accessKey
) {}
