package br.com.infodive.infodive_api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MicrosoftEntraIdService {

    @Value("${jwt.mock-entra-id:false}")
    private boolean mockEntraId;

    private static final String JWKS_URL = "https://login.microsoftonline.com/common/discovery/v2.0/keys";
    private final Map<String, PublicKey> keyCache = new ConcurrentHashMap<>();

    public record EntraIdUser(String email, String nome) {}

    public EntraIdUser validateAndExtract(String idToken) {
        if (mockEntraId) {
            log.info("Bypass da autenticação do Microsoft Entra ID ativo (modo Mock)");
            if (idToken != null && idToken.startsWith("mock:")) {
                String email = idToken.substring(5);
                String nome = email.split("@")[0];
                return new EntraIdUser(email, nome);
            }
            throw new IllegalArgumentException("Token Mock inválido. Deve começar com 'mock:email@dominio.com'");
        }

        try {
            // 1. Decodificar o header sem validar a assinatura primeiro para extrair o 'kid' (Key ID)
            int firstDot = idToken.indexOf('.');
            if (firstDot == -1) {
                throw new IllegalArgumentException("Formato de token JWT inválido");
            }
            String headerJson = new String(Base64.getUrlDecoder().decode(idToken.substring(0, firstDot)), StandardCharsets.UTF_8);
            
            // Parsing simples de JSON para obter o 'kid'
            String kid = extractJsonField(headerJson, "kid");
            if (kid == null) {
                throw new IllegalArgumentException("Token não possui a propriedade 'kid' no header");
            }

            // 2. Obter a chave pública do cache ou da Microsoft
            PublicKey publicKey = getPublicKey(kid);
            if (publicKey == null) {
                throw new IllegalArgumentException("Chave pública correspondente ao 'kid' não encontrada no Microsoft JWKS");
            }

            // 3. Validar o token e extrair as claims usando a chave pública
            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(idToken)
                    .getPayload();

            // 4. Extrair e-mail e nome (tenta claims padrão do Azure AD: email, preferred_username, upn, name)
            String email = claims.get("email", String.class);
            if (email == null) {
                email = claims.get("preferred_username", String.class);
            }
            if (email == null) {
                email = claims.get("upn", String.class);
            }
            if (email == null) {
                email = claims.getSubject();
            }

            String nome = claims.get("name", String.class);
            if (nome == null) {
                nome = email.split("@")[0];
            }

            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Não foi possível extrair um identificador de e-mail do token do Microsoft Entra ID");
            }

            return new EntraIdUser(email, nome);

        } catch (Exception e) {
            log.error("Erro ao validar token do Microsoft Entra ID: {}", e.getMessage());
            throw new IllegalArgumentException("Falha na validação do token do Microsoft Entra ID: " + e.getMessage(), e);
        }
    }

    private PublicKey getPublicKey(String kid) throws Exception {
        if (keyCache.containsKey(kid)) {
            return keyCache.get(kid);
        }

        // Se não encontrar no cache, recarrega as chaves da Microsoft
        refreshCache();
        return keyCache.get(kid);
    }

    private synchronized void refreshCache() {
        try {
            log.info("Buscando chaves públicas (JWKS) do Microsoft Entra ID em: {}", JWKS_URL);
            URL url = new URL(JWKS_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                parseAndCacheJwks(response.toString());
            }
        } catch (Exception e) {
            log.error("Falha ao atualizar o cache de chaves JWKS do Microsoft Entra ID: {}", e.getMessage());
        }
    }

    private void parseAndCacheJwks(String jwksJson) throws Exception {
        // Encontra todas as chaves no JSON
        // Para simplificar e evitar adicionar bibliotecas pesadas de parsing JSON,
        // vamos fazer um parsing rústico, pois a estrutura do JWKS da Microsoft é bem previsível.
        String[] keys = jwksJson.split("\\{\"kty\"");
        for (String keyBlock : keys) {
            if (!keyBlock.contains("\"kid\"")) continue;
            
            String kid = extractJsonField(keyBlock, "kid");
            String nStr = extractJsonField(keyBlock, "n");
            String eStr = extractJsonField(keyBlock, "e");
            String kty = extractJsonField(keyBlock, "kty");

            // Se for nulo por causa do prefixo "{"kty"", tenta sem aspas
            if (kty == null) kty = "RSA"; 

            if ("RSA".equalsIgnoreCase(kty) && kid != null && nStr != null && eStr != null) {
                byte[] modulusBytes = Base64.getUrlDecoder().decode(nStr);
                byte[] exponentBytes = Base64.getUrlDecoder().decode(eStr);
                
                RSAPublicKeySpec spec = new RSAPublicKeySpec(
                    new BigInteger(1, modulusBytes), 
                    new BigInteger(1, exponentBytes)
                );
                
                KeyFactory factory = KeyFactory.getInstance("RSA");
                PublicKey publicKey = factory.generatePublic(spec);
                keyCache.put(kid, publicKey);
            }
        }
        log.info("Cache de chaves JWKS atualizado. Total de chaves carregadas: {}", keyCache.size());
    }

    private String extractJsonField(String json, String field) {
        String pattern = "\"" + field + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}
