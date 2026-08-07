package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.entity.RedeSocialPost;
import br.com.infodive.infodive_api.entity.RedeSocialPost.Rede;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkedinApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${social.linkedin.access-token:}")
    private String envAccessToken;

    @Value("${social.linkedin.organization-id:}")
    private String envOrgId;

    public List<RedeSocialPost> fetchRecentPosts(String configAccessToken, String configOrgId) {
        String token = (configAccessToken != null && !configAccessToken.isBlank()) ? configAccessToken : envAccessToken;
        String orgId = (configOrgId != null && !configOrgId.isBlank()) ? configOrgId : envOrgId;

        if (token == null || token.isBlank()) {
            String sys = System.getenv("SOCIAL_LINKEDIN_ACCESS_TOKEN");
            if (sys == null || sys.isBlank()) sys = System.getenv("LINKEDIN_ACCESS_TOKEN");
            if (sys != null && !sys.isBlank()) token = sys;
        }

        if (orgId == null || orgId.isBlank()) {
            String sys = System.getenv("SOCIAL_LINKEDIN_ORGANIZATION_ID");
            if (sys == null || sys.isBlank()) sys = System.getenv("LINKEDIN_ORGANIZATION_ID");
            if (sys != null && !sys.isBlank()) orgId = sys;
        }

        if (token == null || token.isBlank() || orgId == null || orgId.isBlank()) {
            log.info("LinkedIn API: credenciais não configuradas (token ou organizationId ausentes). Ignorando busca remota.");
            return List.of();
        }

        token = token.replaceAll("\\s+", "").replace("\"", "").replace("'", "").trim();
        orgId = orgId.replaceAll("\\s+", "").replace("\"", "").replace("'", "").trim();

        String cleanOrgId = orgId.startsWith("urn:li:organization:") ? orgId : "urn:li:organization:" + orgId;
        log.info("LinkedIn API: tentando buscar posts para organização {}", cleanOrgId);

        List<String> urlsToTry = List.of(
                "https://api.linkedin.com/v2/ugcPosts?q=authors&authors=List(" + cleanOrgId + ")",
                "https://api.linkedin.com/v2/shares?q=owners&owners=" + cleanOrgId,
                "https://api.linkedin.com/v2/shares?q=owners&owners=List(" + cleanOrgId + ")",
                "https://api.linkedin.com/rest/posts?author=" + cleanOrgId + "&q=author",
                "https://api.linkedin.com/v2/posts?author=" + cleanOrgId + "&q=author"
        );

        for (String url : urlsToTry) {
            try {
                log.info("LinkedIn API: requisitando {}", url);
                List<RedeSocialPost> posts = doFetch(url, token);
                if (!posts.isEmpty()) {
                    log.info("LinkedIn API: Sucesso! {} posts recuperados de {}", posts.size(), url);
                    return posts;
                }
            } catch (HttpStatusCodeException e) {
                log.warn("LinkedIn API: erro HTTP (code {}) em {}: {}", e.getStatusCode(), url, e.getResponseBodyAsString());
            } catch (Exception e) {
                log.warn("LinkedIn API: falha em {}: {}", url, e.getMessage());
            }
        }

        log.error("LinkedIn API: todas as tentativas de busca falharam. Verifique as permissões do aplicativo no LinkedIn Developer Portal.");
        return List.of();
    }

    private List<RedeSocialPost> doFetch(String url, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("X-Restli-Protocol-Version", "2.0.0");
        headers.set("LinkedIn-Version", "202401");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, JsonNode.class);

        JsonNode response = responseEntity.getBody();
        if (response == null || !response.has("elements")) {
            log.warn("LinkedIn API: resposta sem campo 'elements' em {}. Response: {}", url, response);
            return List.of();
        }

        List<RedeSocialPost> posts = new ArrayList<>();
        for (JsonNode item : response.get("elements")) {
            String externalId = item.has("id") ? item.get("id").asText() : null;
            if (externalId == null || externalId.isBlank()) continue;

            String text = extractText(item);
            if (text.isBlank()) continue;

            String imageUrl = extractImage(item);
            String permalink = "https://www.linkedin.com/feed/update/" + externalId;

            LocalDateTime publicadoEm = extractTimestamp(item);

            int likes = 0;
            int comments = 0;
            if (item.has("socialDetail")) {
                JsonNode social = item.get("socialDetail");
                if (social.has("totalLikes")) likes = social.get("totalLikes").asInt(0);
                if (social.has("totalComments")) comments = social.get("totalComments").asInt(0);
            }

            RedeSocialPost post = RedeSocialPost.builder()
                    .rede(Rede.LINKEDIN)
                    .externalId(externalId)
                    .textoLegenda(text)
                    .imagemUrl(imageUrl)
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

    private String extractText(JsonNode item) {
        if (item.has("commentary")) {
            return item.get("commentary").asText("");
        }
        if (item.has("text") && item.get("text").has("text")) {
            return item.get("text").get("text").asText("");
        }
        if (item.has("specificContent")) {
            JsonNode ugc = item.get("specificContent").get("com.linkedin.ugc.ShareContent");
            if (ugc != null && ugc.has("shareCommentary") && ugc.get("shareCommentary").has("text")) {
                return ugc.get("shareCommentary").get("text").asText("");
            }
        }
        return "";
    }

    private String extractImage(JsonNode item) {
        if (item.has("content") && item.get("content").has("media") && item.get("content").get("media").has("id")) {
            return item.get("content").get("media").get("id").asText("");
        }
        if (item.has("specificContent")) {
            JsonNode ugc = item.get("specificContent").get("com.linkedin.ugc.ShareContent");
            if (ugc != null && ugc.has("media") && ugc.get("media").isArray() && !ugc.get("media").isEmpty()) {
                JsonNode m = ugc.get("media").get(0);
                if (m.has("originalUrl")) return m.get("originalUrl").asText("");
                if (m.has("media")) return m.get("media").asText("");
            }
        }
        return "";
    }

    private LocalDateTime extractTimestamp(JsonNode item) {
        if (item.has("publishedAt")) {
            long publishedTs = item.get("publishedAt").asLong();
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(publishedTs), ZoneId.systemDefault());
        }
        if (item.has("created") && item.get("created").has("time")) {
            long createdTs = item.get("created").get("time").asLong();
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(createdTs), ZoneId.systemDefault());
        }
        if (item.has("createdAt")) {
            long createdTs = item.get("createdAt").asLong();
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(createdTs), ZoneId.systemDefault());
        }
        return LocalDateTime.now();
    }
}
