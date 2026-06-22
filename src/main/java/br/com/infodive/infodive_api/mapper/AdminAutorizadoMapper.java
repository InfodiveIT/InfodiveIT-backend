package br.com.infodive.infodive_api.mapper;

import br.com.infodive.infodive_api.dto.request.AdminAutorizadoRequest;
import br.com.infodive.infodive_api.dto.response.AdminAutorizadoResponse;
import br.com.infodive.infodive_api.entity.AdminAutorizado;
import org.springframework.stereotype.Component;

@Component
public class AdminAutorizadoMapper {

    public AdminAutorizadoResponse toResponse(AdminAutorizado entity) {
        return new AdminAutorizadoResponse(
            entity.getId(),
            entity.getEmail(),
            entity.getNome(),
            entity.isAtivo(),
            entity.getCriadoEm()
        );
    }

    public AdminAutorizado toEntity(AdminAutorizadoRequest request) {
        return AdminAutorizado.builder()
            .email(request.email())
            .nome(request.nome())
            .ativo(true)
            .build();
    }

    public void updateEntity(AdminAutorizado entity, AdminAutorizadoRequest request) {
        entity.setEmail(request.email());
        entity.setNome(request.nome());
    }
}
