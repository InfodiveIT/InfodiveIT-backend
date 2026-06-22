package br.com.infodive.infodive_api.dto.response;

public record ConfigBlogResponse(
        String artigosEyebrow,
        String artigosHeadline,
        String socialEyebrow,
        String socialHeadline,
        String socialDescricao,
        String urlInstagram,
        String urlLinkedin
) {}
