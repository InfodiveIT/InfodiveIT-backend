package br.com.infodive.infodive_api.config;

import br.com.infodive.infodive_api.entity.ParceiroToken;
import br.com.infodive.infodive_api.service.JwtService;
import br.com.infodive.infodive_api.service.ParceiroTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ParceiroTokenService parceiroTokenService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String tokenStr = authHeader.substring(7).trim();
        try {
            if (jwtService.isTokenValid(tokenStr)) {
                String email = jwtService.extractEmail(tokenStr);
                String role = jwtService.extractRole(tokenStr);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role != null ? role : "ROLE_ADMIN");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            Collections.singletonList(authority)
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Autenticação JWT válida no Spring Security [{}] para o usuário: {}", role, email);
                }
            } else {
                autenticarParceiroSeValido(tokenStr, request);
            }
        } catch (Exception e) {
            // Se falhou ao interpretar como JWT (ex: formato de token simples), tenta como Token de Parceiro
            autenticarParceiroSeValido(tokenStr, request);
        }

        filterChain.doFilter(request, response);
    }

    private void autenticarParceiroSeValido(String tokenStr, HttpServletRequest request) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        Optional<ParceiroToken> parceiroOpt = parceiroTokenService.validarToken(tokenStr);
        if (parceiroOpt.isPresent()) {
            ParceiroToken parceiro = parceiroOpt.get();
            String role = parceiro.getRole() != null ? parceiro.getRole() : "ROLE_BLOGGER";
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    parceiro.getEmail(),
                    null,
                    Collections.singletonList(authority)
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info("Autenticação via Token de Parceiro [{}] concedida para [{}] ({})", role, parceiro.getNomeAgencia(), parceiro.getEmail());
        } else {
            log.warn("Token invalido ou expirado recebido na requisicao: {}", request.getRequestURI());
        }
    }
}
