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
        String token = (configAccessToken != null && !configAccessToken.isBlank()) ? configAccessToken : envAccessToken;
        String accountId = (configAccountId != null && !configAccountId.isBlank()) ? configAccountId : envAccountId;

        if (token == null || token.isBlank()) {
            String sys = System.getenv("SOCIAL_INSTAGRAM_ACCESS_TOKEN");
            if (sys == null || sys.isBlank()) sys = System.getenv("INSTAGRAM_ACCESS_TOKEN");
            if (sys != null && !sys.isBlank()) token = sys;
        }

        if (accountId == null || accountId.isBlank()) {
            String sys = System.getenv("SOCIAL_INSTAGRAM_ACCOUNT_ID");
            if (sys == null || sys.isBlank()) sys = System.getenv("INSTAGRAM_ACCOUNT_ID");
            if (sys != null && !sys.isBlank()) accountId = sys;
        }

        if (token == null || token.isBlank()) {
            log.info("Instagram API: Access Token não configurado.");
            return List.of();
        }

        // Sanitizar token e accountId contra quebras de linha ou aspas acidentais
        token = token.replaceAll("\\s+", "").replace("\"", "").replace("'", "").trim();
        if (accountId != null) {
            accountId = accountId.replaceAll("\\s+", "").replace("\"", "").replace("'", "").trim();
        }

        String fields = "id,caption,media_type,media_url,permalink,timestamp,like_count,comments_count,thumbnail_url";

        // Tentar endpoints tanto do Facebook Graph quanto do Instagram Graph
        List<String> baseUrls = new ArrayList<>();
        if (accountId != null && !accountId.isBlank()) {
            baseUrls.add("https://graph.facebook.com/v20.0/" + accountId + "/media");
            baseUrls.add("https://graph.instagram.com/v20.0/" + accountId + "/media");
            baseUrls.add("https://graph.instagram.com/" + accountId + "/media");
        }
        baseUrls.add("https://graph.facebook.com/v20.0/me/media");
        baseUrls.add("https://graph.instagram.com/v20.0/me/media");
        baseUrls.add("https://graph.instagram.com/me/media");

        for (String baseUrl : baseUrls) {
            try {
                log.info("Instagram API: tentando buscar via {}", baseUrl);
                List<RedeSocialPost> posts = doFetch(baseUrl, token, fields);
                if (!posts.isEmpty()) {
                    log.info("Instagram API: Sucesso! {} posts recuperados de {}", posts.size(), baseUrl);
                    return posts;
                }
            } catch (HttpStatusCodeException e) {
                log.warn("Instagram API: erro HTTP (code {}) em {}: {}", e.getStatusCode(), baseUrl, e.getResponseBodyAsString());
            } catch (Exception e) {
                log.warn("Instagram API: falha em {}: {}", baseUrl, e.getMessage());
            }
        }

        // Tentar com campos reduzidos para APIs do Instagram que não suportam like_count/comments_count
        String basicFields = "id,caption,media_type,media_url,permalink,timestamp";
        for (String baseUrl : List.of("https://graph.instagram.com/me/media", "https://graph.facebook.com/v20.0/me/media")) {
            try {
                log.info("Instagram API [Fallback Básico]: tentando {}", baseUrl);
                List<RedeSocialPost> posts = doFetch(baseUrl, token, basicFields);
                if (!posts.isEmpty()) {
                    log.info("Instagram API: Sucesso no fallback básico! {} posts recuperados de {}", posts.size(), baseUrl);
                    return posts;
                }
            } catch (Exception e) {
                log.warn("Instagram API [Fallback Básico]: falha em {}: {}", baseUrl, e.getMessage());
            }
        }

        log.error("Instagram API: todas as tentativas de busca falharam. Verifique a validade do Token.");
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
            log.warn("Instagram API: resposta vazia ou sem campo 'data' em {}. Response: {}", baseUrl, response);
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

        return posts;
    }
}
