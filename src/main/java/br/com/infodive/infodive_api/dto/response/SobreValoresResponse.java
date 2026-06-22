package br.com.infodive.infodive_api.dto.response;

import br.com.infodive.infodive_api.entity.ValorItem;
import java.util.List;

public record SobreValoresResponse(
        String eyebrow,
        String headline,
        String paragrafo,
        List<ValorItem> valores
) {}
