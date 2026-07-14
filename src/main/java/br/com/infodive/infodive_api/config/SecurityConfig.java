package br.com.infodive.infodive_api.config;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuração de segurança da Fase 1: libera todos os GETs públicos de leitura e o POST de leads,
 * exige autenticação para o restante. CORS habilitado para as origens configuradas.
 * <p>
 * Os paths abaixo são relativos ao context-path {@code /api/v1} definido em application.properties.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .contentTypeOptions(contentType -> {})
                        .frameOptions(frame -> frame.deny())
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)))
                .authorizeHttpRequests(auth -> auth
                        // Login público
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        // Uploads públicos
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                        // Endpoints públicos de leitura
                        .requestMatchers(HttpMethod.GET, "/fabricantes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/solucoes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/produtos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/servicos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/conteudos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/cases/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/paginas-hero/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/ctas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/config-footer/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/config-blog/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/contato-info/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/faq/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/servicos-etapas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/servicos-metodologia/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/sobre-cultura/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/sobre-numeros/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/sobre-timeline/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/sobre-valores/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/secoes-home/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/hero-carousel/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/home-solucoes-bento/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/home-seguranca-marquee/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/home-problemas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/home-trust-stats/**").permitAll()
                        // Lead: POST público
                        .requestMatchers(HttpMethod.POST, "/leads").permitAll()
                        // Tudo mais requer autenticação
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
            "http://localhost:*",
            "http://127.0.0.1:*",
            "https://*.infodive.com.br",
            "https://infodive.com.br"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
