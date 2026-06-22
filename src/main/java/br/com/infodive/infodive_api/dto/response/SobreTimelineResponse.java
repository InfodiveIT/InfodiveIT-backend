package br.com.infodive.infodive_api.dto.response;

import br.com.infodive.infodive_api.entity.MarcoItem;
import java.util.List;

public record SobreTimelineResponse(
        String eyebrow,
        String headline,
        List<MarcoItem> marcos
) {}
