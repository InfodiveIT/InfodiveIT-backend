package br.com.infodive.infodive_api.dto.request;

import br.com.infodive.infodive_api.entity.ConteudoBloco;
import br.com.infodive.infodive_api.entity.OrigemConteudo;
import br.com.infodive.infodive_api.entity.TipoConteudo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ConteudoRequest(
        @NotBlank String titulo,
        String slug,
        @NotNull TipoConteudo tipo,
        @NotNull OrigemConteudo origem,
        String descricao,
        Object conteudo,
        String imagemUrl,
        String urlExterna,
        String socialPostId,
        String autor,
        String tempoLeitura,
        LocalDateTime publicadoEm,
        UUID categoriaId,
        UUID fabricanteId,
        UUID produtoId,
        boolean destaque
) {}
