package br.com.infodive.infodive_api.mapper;

import br.com.infodive.infodive_api.dto.response.LogAuditoriaResponse;
import br.com.infodive.infodive_api.entity.LogAuditoria;
import org.springframework.stereotype.Component;

@Component
public class LogAuditoriaMapper {

    public LogAuditoriaResponse toResponse(LogAuditoria entity) {
        if (entity == null) return null;

        return new LogAuditoriaResponse(
                entity.getId(),
                entity.getUsuarioEmail(),
                entity.getUsuarioNome(),
                entity.getAcao(),
                entity.getRecurso(),
                entity.getRecursoId(),
                entity.getDetalhes(),
                entity.getCriadoEm()
        );
    }
}
