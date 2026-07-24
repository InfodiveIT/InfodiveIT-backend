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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // 1. Valida o token Entra ID e extrai email e nome
        EntraIdUser entraUser = entraIdService.validateAndExtract(request.idToken());

        String emailLower = entraUser.email().toLowerCase().trim();
        String role;

        // 2. Determina o perfil (Role) do usuário
        if (emailLower.endsWith("@infodive.com.br")) {
            // Colaboradores internos Infodive recebem perfil de Administrador Geral
            role = "ROLE_ADMIN";
        } else if (isBloggerAutorizado(emailLower)) {
            // Agências terceiras / Editores externos recebem perfil estrito de Blog
            role = "ROLE_BLOGGER";
        } else {
            throw new AcessoNegadoException("Acesso negado: Conta sem permissão de acesso ao painel.");
        }

        // 3. Gera o JWT local contendo a Role apropriada
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
