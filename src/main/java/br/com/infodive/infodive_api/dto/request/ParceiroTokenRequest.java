package br.com.infodive.infodive_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ParceiroTokenRequest(
        @NotBlank(message = "Nome da agência/usuário é obrigatório")
        String nomeAgencia,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        String role, // ex: "ROLE_BLOGGER" ou "ROLE_ADMIN"

        Integer diasValidade // ex: 15, 30, 90, 365, ou null/0 para sem expiração
) {}
