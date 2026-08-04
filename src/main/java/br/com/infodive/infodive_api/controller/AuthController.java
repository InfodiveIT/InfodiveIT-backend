package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.LoginRequest;
import br.com.infodive.infodive_api.dto.response.LoginResponse;
import br.com.infodive.infodive_api.entity.ParceiroToken;
import br.com.infodive.infodive_api.exception.AcessoNegadoException;
import br.com.infodive.infodive_api.service.JwtService;
import br.com.infodive.infodive_api.service.MicrosoftEntraIdService;
import br.com.infodive.infodive_api.service.MicrosoftEntraIdService.EntraIdUser;
import br.com.infodive.infodive_api.service.ParceiroTokenService;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    private final ParceiroTokenService parceiroTokenService;

    @Value("${auth.blogger-emails:}")
    private String bloggerEmailsConfig;

    @Value("${auth.blogger-domains:}")
    private String bloggerDomainsConfig;

    @Value("${auth.partner-access-key:infodive_partner_2026}")
    private String partnerAccessKey;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // 1. Autenticação de Parceiros Externos / Agências via Token do Banco de Dados ou Chave Legada
        if (request.accessKey() != null && !request.accessKey().isBlank()) {
            String cleanKey = request.accessKey().trim();

            // 1.1 Tenta validar no novo sistema de ParceiroToken dinâmico
            Optional<ParceiroToken> parceiroOpt = parceiroTokenService.validarToken(cleanKey);
            if (parceiroOpt.isPresent()) {
                ParceiroToken parceiro = parceiroOpt.get();
                String role = parceiro.getRole() != null ? parceiro.getRole() : "ROLE_BLOGGER";
                if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }
                String localToken = jwtService.generateToken(parceiro.getEmail(), parceiro.getNomeAgencia(), role);
                return ResponseEntity.ok(new LoginResponse(localToken, parceiro.getEmail(), parceiro.getNomeAgencia(), role));
            }

            // 1.2 Fallback para chave de acesso legada do Railway (caso ainda utilizada)
            if (request.email() != null && !request.email().isBlank()) {
                String partnerEmail = request.email().toLowerCase().trim();
                if (cleanKey.equals(partnerAccessKey) && isBloggerAutorizado(partnerEmail)) {
                    String partnerName = partnerEmail.split("@")[0];
                    String role = "ROLE_BLOGGER";
                    String localToken = jwtService.generateToken(partnerEmail, partnerName, role);
                    return ResponseEntity.ok(new LoginResponse(localToken, partnerEmail, partnerName, role));
                }
            }

            throw new AcessoNegadoException("Token ou chave de acesso de parceiro inválida ou expirada.");
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
