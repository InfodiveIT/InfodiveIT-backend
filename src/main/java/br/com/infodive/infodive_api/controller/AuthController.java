package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.LoginRequest;
import br.com.infodive.infodive_api.dto.response.LoginResponse;
import br.com.infodive.infodive_api.exception.AcessoNegadoException;
import br.com.infodive.infodive_api.service.JwtService;
import br.com.infodive.infodive_api.service.MicrosoftEntraIdService;
import br.com.infodive.infodive_api.service.MicrosoftEntraIdService.EntraIdUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // 1. Valida o token Entra ID e extrai email e nome
        EntraIdUser entraUser = entraIdService.validateAndExtract(request.idToken());

        // 2. Valida se o e-mail pertence ao domínio corporativo @infodive.com.br
        String emailLower = entraUser.email().toLowerCase().trim();
        if (!emailLower.endsWith("@infodive.com.br")) {
            throw new AcessoNegadoException("Acesso negado: Apenas contas corporativas com domínio @infodive.com.br têm permissão de acesso ao painel.");
        }

        // 3. Gera o JWT local assinado pelo sistema para consumo das APIs administrativas
        String localToken = jwtService.generateToken(entraUser.email(), entraUser.nome());

        return ResponseEntity.ok(new LoginResponse(localToken, entraUser.email(), entraUser.nome()));
    }
}
