package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.entity.RedeSocialPost;
import br.com.infodive.infodive_api.entity.RedeSocialPost.Rede;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
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

        String orgIdNum = orgId.replace("urn:li:organization:", "");
        String cleanOrgUrn = "urn:li:organization:" + orgIdNum;
        log.info("LinkedIn API: tentando buscar posts para organização {}", cleanOrgUrn);

        List<String> urlsToTry = List.of(
                "https://api.linkedin.com/v2/ugcPosts?q=authors&authors=List(" + cleanOrgUrn + ")",
                "https://api.linkedin.com/v2/shares?q=owners&owners=" + cleanOrgUrn,
                "https://api.linkedin.com/v2/shares?q=owners&owners=List(" + cleanOrgUrn + ")",
                "https://api.linkedin.com/v2/posts?author=" + cleanOrgUrn + "&q=author",
                "https://api.linkedin.com/rest/posts?author=" + cleanOrgUrn + "&q=author"
        );

        for (String urlStr : urlsToTry) {
            try {
                log.info("LinkedIn API: requisitando {}", urlStr);
                URI uri = URI.create(urlStr);
                List<RedeSocialPost> posts = doFetch(uri, token, urlStr.contains("/rest/"));
                if (!posts.isEmpty()) {
                    log.info("LinkedIn API: Sucesso! {} posts recuperados de {}", posts.size(), urlStr);
                    return posts;
                }
            } catch (HttpStatusCodeException e) {
                log.warn("LinkedIn API: erro HTTP (code {}) em {}: {}", e.getStatusCode(), urlStr, e.getResponseBodyAsString());
            } catch (Exception e) {
                log.warn("LinkedIn API: falha em {}: {}", urlStr, e.getMessage());
            }
        }

        log.error("LinkedIn API: todas as tentativas de busca falharam. Verifique se o produto 'Community Management API' ou 'Share on LinkedIn' está ativo no LinkedIn Developer Portal.");
        return List.of();
    }

    private List<RedeSocialPost> doFetch(URI uri, String token, boolean isRest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("X-Restli-Protocol-Version", "2.0.0");
        if (isRest) {
            headers.set("LinkedIn-Version", "202401");
        }

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, JsonNode.class);

        JsonNode response = responseEntity.getBody();
        if (response == null || !response.has("elements")) {
            log.warn("LinkedIn API: resposta sem campo 'elements' em {}. Response: {}", uri, response);
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
