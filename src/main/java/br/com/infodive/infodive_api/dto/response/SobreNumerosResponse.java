package br.com.infodive.infodive_api.dto.response;

import br.com.infodive.infodive_api.entity.StatItem;
import java.util.List;

public record SobreNumerosResponse(
        String textoDescritivo,
        List<StatItem> stats
) {}
