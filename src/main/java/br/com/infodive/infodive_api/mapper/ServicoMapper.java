package br.com.infodive.infodive_api.mapper;

import br.com.infodive.infodive_api.dto.request.ServicoRequest;
import br.com.infodive.infodive_api.dto.response.ServicoResponse;
import br.com.infodive.infodive_api.entity.Servico;
import org.springframework.stereotype.Component;

@Component
public class ServicoMapper {

    public ServicoResponse toResponse(Servico entity) {
        return new ServicoResponse(
                entity.getId(),
                entity.getNome(),
                entity.getSlug(),
                entity.getDescricao(),
                entity.getIcone(),
                entity.getOrdem(),
                entity.isAtivo(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public Servico toEntity(ServicoRequest request) {
        return Servico.builder()
                .slug(request.slug())
                .nome(request.nome())
                .descricao(request.descricao())
                .icone(request.icone())
                .ordem(request.ordem())
                .build();
    }

    public void updateEntity(Servico entity, ServicoRequest request) {
        entity.setNome(request.nome());
        entity.setDescricao(request.descricao());
        entity.setIcone(request.icone());
        entity.setOrdem(request.ordem());
    }
}
