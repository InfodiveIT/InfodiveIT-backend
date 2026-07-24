package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.LoginRequest;
import br.com.infodive.infodive_api.dto.response.LoginResponse;
import br.com.infodive.infodive_api.exception.AcessoNegadoException;
import br.com.infodive.infodive_api.service.JwtService;
import br.com.infodive.infodive_api.service.MicrosoftEntraIdService;
import br.com.infodive.infodive_api.service.MicrosoftEntraIdService.EntraIdUser;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MicrosoftEntraIdService entraIdService;
    private final JwtService jwtService;

    @Value("${auth.blogger-emails:}")
    private String bloggerEmailsConfig;

    @Value("${auth.blogger-domains:}")
    private String bloggerDomainsConfig;

    @Value("${auth.partner-access-key:infodive_partner_2026}")
    private String partnerAccessKey;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // 1. Autenticação de Parceiros Externos / Agências (E-mail + Chave de Acesso)
        if (request.email() != null && !request.email().isBlank() && request.accessKey() != null) {
            String partnerEmail = request.email().toLowerCase().trim();

            if (!request.accessKey().equals(partnerAccessKey)) {
                throw new AcessoNegadoException("Chave de acesso do parceiro inválida.");
            }

            if (!isBloggerAutorizado(partnerEmail)) {
                throw new AcessoNegadoException("E-mail de parceiro não cadastrado na lista de editores permitidos.");
            }

            String partnerName = partnerEmail.split("@")[0];
            String role = "ROLE_BLOGGER";
            String localToken = jwtService.generateToken(partnerEmail, partnerName, role);

            return ResponseEntity.ok(new LoginResponse(localToken, partnerEmail, partnerName, role));
        }

        // 2. Autenticação Oficial Microsoft Entra ID (Colaboradores Infodive)
        if (request.idToken() == null || request.idToken().isBlank()) {
            throw new IllegalArgumentException("Token de autenticação não fornecido.");
        }

        EntraIdUser entraUser = entraIdService.validateAndExtract(request.idToken());
        String emailLower = entraUser.email().toLowerCase().trim();
        String role;

        if (emailLower.endsWith("@infodive.com.br")) {
            role = "ROLE_ADMIN";
        } else if (isBloggerAutorizado(emailLower)) {
            role = "ROLE_BLOGGER";
        } else {
            throw new AcessoNegadoException("Acesso negado: Apenas e-mails autorizados têm acesso ao painel.");
        }

        String localToken = jwtService.generateToken(entraUser.email(), entraUser.nome(), role);
        return ResponseEntity.ok(new LoginResponse(localToken, entraUser.email(), entraUser.nome(), role));
    }

    private boolean isBloggerAutorizado(String email) {
        if (bloggerEmailsConfig != null && !bloggerEmailsConfig.isBlank()) {
            List<String> emails = Arrays.stream(bloggerEmailsConfig.split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toList();
            if (emails.contains(email)) return true;
        }

        if (bloggerDomainsConfig != null && !bloggerDomainsConfig.isBlank()) {
            List<String> domains = Arrays.stream(bloggerDomainsConfig.split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toList();
            for (String domain : domains) {
                if (!domain.isEmpty() && email.endsWith("@" + domain.replace("@", ""))) {
                    return true;
                }
            }
        }

        return false;
    }
}
