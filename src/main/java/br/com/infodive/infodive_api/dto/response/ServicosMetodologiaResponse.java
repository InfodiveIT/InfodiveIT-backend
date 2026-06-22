package br.com.infodive.infodive_api.dto.response;

import br.com.infodive.infodive_api.entity.MetricaItem;
import br.com.infodive.infodive_api.entity.PilarItem;
import java.util.List;

public record ServicosMetodologiaResponse(
        String eyebrow,
        String headline,
        String paragrafo,
        List<MetricaItem> metricas,
        List<PilarItem> pilares
) {}
