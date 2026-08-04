package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.entity.RedeSocialPost;
import br.com.infodive.infodive_api.entity.RedeSocialPost.Rede;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstagramApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${social.instagram.access-token:}")
    private String envAccessToken;

    @Value("${social.instagram.account-id:}")
    private String envAccountId;

    public List<RedeSocialPost> fetchRecentPosts(String configAccessToken, String configAccountId) {
        String token = (configAccessToken != null && !configAccessToken.isBlank()) ? configAccessToken.trim() : (envAccessToken != null ? envAccessToken.trim() : "");
        String accountId = (configAccountId != null && !configAccountId.isBlank()) ? configAccountId.trim() : (envAccountId != null ? envAccountId.trim() : "");

        if (token.isBlank()) {
            String sys = System.getenv("SOCIAL_INSTAGRAM_ACCESS_TOKEN");
            if (sys == null || sys.isBlank()) sys = System.getenv("INSTAGRAM_ACCESS_TOKEN");
            if (sys != null && !sys.isBlank()) token = sys.trim();
        }

        if (accountId.isBlank()) {
            String sys = System.getenv("SOCIAL_INSTAGRAM_ACCOUNT_ID");
            if (sys == null || sys.isBlank()) sys = System.getenv("INSTAGRAM_ACCOUNT_ID");
            if (sys != null && !sys.isBlank()) accountId = sys.trim();
        }

        if (token.isBlank()) {
            log.info("Instagram Graph API: Access Token não configurado.");
            return List.of();
        }

        String fields = "id,caption,media_type,media_url,permalink,timestamp,like_count,comments_count,thumbnail_url";

        // Estratégia 1: Se temos accountId, tenta GET /{accountId}/media
        if (!accountId.isBlank()) {
            try {
                log.info("Instagram Graph API [Estratégia 1]: buscando /{}/media", accountId);
                return doFetch("https://graph.facebook.com/v20.0/" + accountId + "/media", token, fields);
            } catch (HttpStatusCodeException e) {
                log.warn("Instagram Graph API [Estratégia 1] falhou (HTTP {}): {}", e.getStatusCode(), e.getResponseBodyAsString());
                
                // Estratégia 2: Se accountId for o ID da Página do Facebook, busca o ID do Instagram vinculado
                try {
                    log.info("Instagram Graph API [Estratégia 2]: buscando instagram_business_account para ID {}", accountId);
                    String pageUrl = UriComponentsBuilder
                            .fromHttpUrl("https://graph.facebook.com/v20.0/" + accountId)
                            .queryParam("fields", "instagram_business_account")
                            .queryParam("access_token", token)
                            .toUriString();
                    JsonNode pageResp = restTemplate.getForObject(pageUrl, JsonNode.class);
                    if (pageResp != null && pageResp.has("instagram_business_account") && pageResp.get("instagram_business_account").has("id")) {
                        String realIgId = pageResp.get("instagram_business_account").get("id").asText();
                        log.info("Instagram Graph API [Estratégia 2]: ID real do Instagram encontrado: {}", realIgId);
                        return doFetch("https://graph.facebook.com/v20.0/" + realIgId + "/media", token, fields);
                    }
                } catch (Exception ex) {
                    log.warn("Instagram Graph API [Estratégia 2] falhou: {}", ex.getMessage());
                }
            } catch (Exception e) {
                log.warn("Instagram Graph API [Estratégia 1] erro genérico: {}", e.getMessage());
            }
        }

        // Estratégia 3: Tenta GET /me/media diretamente usando o token
        try {
            log.info("Instagram Graph API [Estratégia 3]: buscando /me/media via token");
            return doFetch("https://graph.facebook.com/v20.0/me/media", token, fields);
        } catch (HttpStatusCodeException e) {
            log.error("Instagram Graph API [Estratégia 3] falhou (HTTP {}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            
            // Tentativa final com campos reduzidos em /me/media
            try {
                log.info("Instagram Graph API [Estratégia 3 Fallback]: buscando /me/media com campos básicos");
                return doFetch("https://graph.facebook.com/v20.0/me/media", token, "id,caption,media_type,media_url,permalink,timestamp");
            } catch (Exception ex) {
                log.error("Instagram Graph API [Estratégia 3 Fallback] falhou: {}", ex.getMessage());
            }
        } catch (Exception e) {
            log.error("Instagram Graph API erro final: {}", e.getMessage(), e);
        }

        return List.of();
    }

    private List<RedeSocialPost> doFetch(String baseUrl, String token, String fields) {
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("fields", fields)
                .queryParam("access_token", token)
                .toUriString();

        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        if (response == null || !response.has("data")) {
            log.warn("Instagram Graph API: resposta vazia ou sem campo 'data' em {}. Response: {}", baseUrl, response);
            return List.of();
        }

        List<RedeSocialPost> posts = new ArrayList<>();
        for (JsonNode item : response.get("data")) {
            String externalId = item.has("id") ? item.get("id").asText() : null;
            if (externalId == null || externalId.isBlank()) continue;

            String caption = item.has("caption") ? item.get("caption").asText() : "";
            String mediaType = item.has("media_type") ? item.get("media_type").asText() : "IMAGE";
            String mediaUrl = "";
            if ("VIDEO".equalsIgnoreCase(mediaType) && item.has("thumbnail_url")) {
                mediaUrl = item.get("thumbnail_url").asText();
            } else if (item.has("media_url")) {
                mediaUrl = item.get("media_url").asText();
            }
            String permalink = item.has("permalink") ? item.get("permalink").asText() : "";
            int likes = item.has("like_count") ? item.get("like_count").asInt(0) : 0;
            int comments = item.has("comments_count") ? item.get("comments_count").asInt(0) : 0;

            LocalDateTime publicadoEm = LocalDateTime.now();
            if (item.has("timestamp")) {
                try {
                    String ts = item.get("timestamp").asText();
                    publicadoEm = ZonedDateTime.parse(ts, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
                } catch (Exception e) {
                    log.debug("Erro ao converter timestamp do Instagram: {}", e.getMessage());
                }
            }

            RedeSocialPost post = RedeSocialPost.builder()
                    .rede(Rede.INSTAGRAM)
                    .externalId(externalId)
                    .textoLegenda(caption)
                    .imagemUrl(mediaUrl)
                    .permalinkUrl(permalink)
                    .likesCount(likes)
                    .commentsCount(comments)
                    .publicadoEm(publicadoEm)
                    .ativo(true)
                    .build();

            posts.add(post);
        }

        log.info("Instagram Graph API: {} posts recuperados com sucesso de {}.", posts.size(), baseUrl);
        return posts;
    }
}
