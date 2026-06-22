package br.com.infodive.infodive_api.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record LeadRequest(
        @NotBlank String nomeCompleto,
        @NotBlank @Email String email,
        String telefone,
        @NotBlank String empresa,
        String cargo,
        String mensagem,
        @AssertTrue(message = "O consentimento LGPD é obrigatório") boolean consentimentoLgpd,
        UUID produtoInteresseId
) {}
