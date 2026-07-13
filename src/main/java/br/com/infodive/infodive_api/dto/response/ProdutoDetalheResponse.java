package br.com.infodive.infodive_api.dto.response;

import br.com.infodive.infodive_api.entity.CasoDeUsoItem;
import br.com.infodive.infodive_api.entity.DiferencialItem;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** Versão completa para /produtos/[slug] — espelha ProdutoDTO do frontend. */
public record ProdutoDetalheResponse(
        UUID id,
        String nome,
        String slug,
        String subcategoria,
        String descricaoCurta,
        String descricaoCompleta,
        List<CasoDeUsoItem> casosDeUso,
        List<DiferencialItem> diferenciais,
        String servicosEyebrow,
        String servicosTitulo,
        String servicosDescricao,
        String imagemUrl,
        boolean destaque,
        boolean ativo,
        UUID categoriaId,
        String categoriaSlug,
        UUID solucaoId,
        String solucaoSlug,
        String solucaoNome,
        UUID fabricanteId,
        String fabricanteSlug,
        String fabricanteNome,
        String fabricanteLogoUrl,
        List<ServicoResumoResponse> servicos,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
