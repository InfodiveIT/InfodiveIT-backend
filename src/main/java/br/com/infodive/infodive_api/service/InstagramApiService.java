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

        if (token == null || token.isBlank() || accountId == null || accountId.isBlank()) {
            log.info("Instagram Graph API: credenciais não configuradas. Ignorando busca remota.");
            return List.of();
        }

        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://graph.facebook.com/v20.0/{accountId}/media")
                    .queryParam("fields", "id,caption,media_type,media_url,permalink,timestamp,like_count,comments_count,thumbnail_url")
                    .queryParam("access_token", token)
                    .buildAndExpand(accountId)
                    .toUriString();

            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response == null || !response.has("data")) {
                log.warn("Instagram Graph API: resposta vazia ou sem campo 'data'.");
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

            log.info("Instagram Graph API: {} posts recuperados com sucesso.", posts.size());
            return posts;
        } catch (Exception e) {
            log.error("Erro ao buscar posts do Instagram: {}", e.getMessage());
            return List.of();
        }
    }
}
