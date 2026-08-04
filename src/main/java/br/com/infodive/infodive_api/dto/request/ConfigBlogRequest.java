package br.com.infodive.infodive_api.dto.request;

public record ConfigBlogRequest(
        String artigosEyebrow,
        String artigosHeadline,
        String socialEyebrow,
        String socialHeadline,
        String socialDescricao,
        String urlInstagram,
        String urlLinkedin,
        String instagramAccessToken,
        String instagramAccountId,
        String linkedinAccessToken,
        String linkedinOrganizationId
) {}
