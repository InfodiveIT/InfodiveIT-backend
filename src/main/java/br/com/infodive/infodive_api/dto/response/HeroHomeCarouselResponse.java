package br.com.infodive.infodive_api.dto.response;

import java.util.UUID;

public record HeroHomeCarouselResponse(
        UUID id,
        String imagemUrl,
        int ordem
) {}
