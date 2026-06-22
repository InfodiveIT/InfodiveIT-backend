package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.LoginRequest;
import br.com.infodive.infodive_api.dto.response.LoginResponse;
import br.com.infodive.infodive_api.exception.AcessoNegadoException;
import br.com.infodive.infodive_api.service.AdminAutorizadoService;
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
    private final AdminAutorizadoService adminService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // 1. Valida o token Entra ID e extrai email e nome
        EntraIdUser entraUser = entraIdService.validateAndExtract(request.idToken());

        // 2. Verifica se o email está na allowlist de admins ativos
        if (!adminService.isEmailAuthorized(entraUser.email())) {
            throw new AcessoNegadoException("E-mail não autorizado para acesso administrativo: " + entraUser.email());
        }

        // 3. Gera o JWT local assinado pelo sistema
        String localToken = jwtService.generateToken(entraUser.email(), entraUser.nome());

        return ResponseEntity.ok(new LoginResponse(localToken, entraUser.email(), entraUser.nome()));
    }
}
