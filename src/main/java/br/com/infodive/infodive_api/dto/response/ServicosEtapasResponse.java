package br.com.infodive.infodive_api.dto.response;

import br.com.infodive.infodive_api.entity.EtapaItem;
import java.util.List;

public record ServicosEtapasResponse(
        String eyebrow,
        String headline,
        String subtitulo,
        List<EtapaItem> etapas
) {}
