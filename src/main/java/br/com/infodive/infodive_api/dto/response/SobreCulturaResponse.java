package br.com.infodive.infodive_api.dto.response;

import br.com.infodive.infodive_api.entity.FotoItem;
import java.util.List;

public record SobreCulturaResponse(
        String eyebrow,
        String headline,
        String paragrafo,
        List<FotoItem> fotos
) {}
