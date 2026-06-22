package br.com.infodive.infodive_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "Microsoft ID Token é obrigatório")
    String idToken
) {}
