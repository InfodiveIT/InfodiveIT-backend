package br.com.infodive.infodive_api.mapper;

import br.com.infodive.infodive_api.dto.response.LeadResponse;
import br.com.infodive.infodive_api.entity.Lead;
import org.springframework.stereotype.Component;

@Component
public class LeadMapper {

    public LeadResponse toResponse(Lead entity) {
        return new LeadResponse(
                entity.getId(),
                entity.getNomeCompleto(),
                entity.getEmail(),
                entity.getTelefone(),
                entity.getEmpresa(),
                entity.getCargo(),
                entity.getMensagem(),
                entity.isConsentimentoLgpd(),
                entity.getProdutoInteresse() != null ? entity.getProdutoInteresse().getId() : null,
                entity.getCriadoEm()
        );
    }
}
