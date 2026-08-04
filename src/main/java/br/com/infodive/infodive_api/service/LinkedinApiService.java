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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

        if (token == null || token.isBlank() || orgId == null || orgId.isBlank()) {
            log.info("LinkedIn API: credenciais não configuradas. Ignorando busca remota.");
            return List.of();
        }

        try {
            String cleanOrgId = orgId.startsWith("urn:li:organization:") ? orgId : "urn:li:organization:" + orgId;
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://api.linkedin.com/v2/posts")
                    .queryParam("author", cleanOrgId)
                    .queryParam("q", "author")
                    .queryParam("sortBy", "CREATED")
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.set("X-Restli-Protocol-Version", "2.0.0");
            headers.set("LinkedIn-Version", "202304");

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, JsonNode.class);

            JsonNode response = responseEntity.getBody();
            if (response == null || !response.has("elements")) {
                log.warn("LinkedIn API: resposta vazia ou sem campo 'elements'.");
                return List.of();
            }

            List<RedeSocialPost> posts = new ArrayList<>();
            for (JsonNode item : response.get("elements")) {
                String externalId = item.has("id") ? item.get("id").asText() : null;
                if (externalId == null || externalId.isBlank()) continue;

                String text = "";
                if (item.has("commentary")) {
                    text = item.get("commentary").asText();
                } else if (item.has("text") && item.get("text").has("text")) {
                    text = item.get("text").get("text").asText();
                }

                String imageUrl = "";
                if (item.has("content") && item.get("content").has("media") && item.get("content").get("media").has("id")) {
                    imageUrl = item.get("content").get("media").get("id").asText();
                }

                String permalink = "https://www.linkedin.com/feed/update/" + externalId;

                LocalDateTime publicadoEm = LocalDateTime.now();
                if (item.has("publishedAt")) {
                    long publishedTs = item.get("publishedAt").asLong();
                    publicadoEm = LocalDateTime.ofInstant(Instant.ofEpochMilli(publishedTs), ZoneId.systemDefault());
                } else if (item.has("createdAt")) {
                    long createdTs = item.get("createdAt").asLong();
                    publicadoEm = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdTs), ZoneId.systemDefault());
                }

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

            log.info("LinkedIn API: {} posts recuperados com sucesso.", posts.size());
            return posts;
        } catch (Exception e) {
            log.error("Erro ao buscar posts do LinkedIn: {}", e.getMessage());
            return List.of();
        }
    }
}
