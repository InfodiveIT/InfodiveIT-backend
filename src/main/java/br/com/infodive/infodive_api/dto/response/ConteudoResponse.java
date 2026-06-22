package br.com.infodive.infodive_api.dto.response;

import br.com.infodive.infodive_api.entity.OrigemConteudo;
import br.com.infodive.infodive_api.entity.TipoConteudo;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Espelha o ConteudoDTO do frontend. {@code conteudo} é JSON serializado em String (contrato tipa como string).
 */
public record ConteudoResponse(
        UUID id,
        String titulo,
        String slug,
        TipoConteudo tipo,
        OrigemConteudo origem,
        String descricao,
        String conteudo,
        String urlExterna,
        String socialPostId,
        LocalDateTime publicadoEm,
        boolean ativo,
        UUID categoriaId,
        UUID fabricanteId,
        UUID produtoId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
