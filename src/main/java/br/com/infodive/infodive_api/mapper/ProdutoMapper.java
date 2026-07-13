package br.com.infodive.infodive_api.mapper;

import br.com.infodive.infodive_api.dto.response.ProdutoDetalheResponse;
import br.com.infodive.infodive_api.dto.response.ProdutoResumoResponse;
import br.com.infodive.infodive_api.dto.response.ServicoResumoResponse;
import br.com.infodive.infodive_api.entity.Produto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {

    public ProdutoResumoResponse toResumoResponse(Produto entity) {
        return new ProdutoResumoResponse(
                entity.getId(),
                entity.getNome(),
                entity.getSlug(),
                entity.getSubcategoria(),
                entity.getDescricaoCurta(),
                entity.getImagemUrl(),
                entity.isDestaque(),
                entity.getCategoria() != null ? entity.getCategoria().getSlug() : null,
                entity.getCategoria() != null ? entity.getCategoria().getNome() : null,
                entity.getSolucao() != null ? entity.getSolucao().getSlug() : null,
                entity.getSolucao() != null ? entity.getSolucao().getTitulo() : null,
                entity.getFabricante() != null ? entity.getFabricante().getSlug() : null,
                entity.getFabricante() != null ? entity.getFabricante().getNome() : null,
                entity.getFabricante() != null ? entity.getFabricante().getLogoUrl() : null
        );
    }

    public ProdutoDetalheResponse toDetalheResponse(Produto entity) {
        List<ServicoResumoResponse> servicos = entity.getServicos() == null
                ? List.of()
                : entity.getServicos().stream()
                        .map(s -> new ServicoResumoResponse(s.getId(), s.getNome(), s.getSlug(), s.getIcone()))
                        .toList();
        return new ProdutoDetalheResponse(
                entity.getId(),
                entity.getNome(),
                entity.getSlug(),
                entity.getSubcategoria(),
                entity.getDescricaoCurta(),
                entity.getDescricaoCompleta(),
                entity.getCasosDeUso(),
                entity.getDiferenciais(),
                entity.getServicosEyebrow(),
                entity.getServicosTitulo(),
                entity.getServicosDescricao(),
                entity.getImagemUrl(),
                entity.isDestaque(),
                entity.isAtivo(),
                entity.getCategoria() != null ? entity.getCategoria().getId() : null,
                entity.getCategoria() != null ? entity.getCategoria().getSlug() : null,
                entity.getSolucao() != null ? entity.getSolucao().getId() : null,
                entity.getSolucao() != null ? entity.getSolucao().getSlug() : null,
                entity.getSolucao() != null ? entity.getSolucao().getTitulo() : null,
                entity.getFabricante() != null ? entity.getFabricante().getId() : null,
                entity.getFabricante() != null ? entity.getFabricante().getSlug() : null,
                entity.getFabricante() != null ? entity.getFabricante().getNome() : null,
                entity.getFabricante() != null ? entity.getFabricante().getLogoUrl() : null,
                servicos,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
