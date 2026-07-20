package br.com.infodive.infodive_api.dto.request;

import br.com.infodive.infodive_api.entity.CasoDeUsoItem;
import br.com.infodive.infodive_api.entity.DiferencialItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record ProdutoRequest(
                @NotBlank String slug,
                @NotBlank String nome,
                String subcategoria,
                String descricaoCurta,
                String descricaoCompleta,
                List<DiferencialItem> diferenciais,
                List<CasoDeUsoItem> casosDeUso,
                String servicosEyebrow,
                String servicosTitulo,
                String servicosDescricao,
                String imagemUrl,
                String linkOficial,
                boolean destaque,
                UUID fabricanteId,
                UUID categoriaId,
                UUID solucaoId,
                @Size(max = 6, message = "Selecione no máximo 6 serviços vinculados")
                List<UUID> servicoIds) {
}
