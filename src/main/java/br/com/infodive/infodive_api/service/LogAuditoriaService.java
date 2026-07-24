package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.response.LogAuditoriaResponse;
import br.com.infodive.infodive_api.entity.LogAuditoria;
import br.com.infodive.infodive_api.mapper.LogAuditoriaMapper;
import br.com.infodive.infodive_api.repository.LogAuditoriaRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogAuditoriaService {

    private final LogAuditoriaRepository repository;
    private final LogAuditoriaMapper mapper;

    // Cache simples em memória para evitar registros duplicados em curto intervalo (< 2s)
    private static final ConcurrentHashMap<String, Long> RECENT_LOGS = new ConcurrentHashMap<>();

    @Transactional(readOnly = true)
    public List<LogAuditoriaResponse> findAll() {
        return repository.findAllByOrderByCriadoEmDesc()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public void registrar(String acao, String recurso, String recursoId, String detalhes) {
        try {
            String usuarioEmail = "sistema@infodive.com.br";
            String usuarioNome = "Sistema";

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                usuarioEmail = auth.getName();
                usuarioNome = usuarioEmail.contains("@") ? usuarioEmail.split("@")[0] : usuarioEmail;
            }

            // Deduplicação: ignora requisições idênticas disparadas em menos de 2 segundos
            String dedupeKey = usuarioEmail + ":" + acao + ":" + recurso + ":" + (recursoId != null ? recursoId : "");
            long now = System.currentTimeMillis();
            Long lastTime = RECENT_LOGS.get(dedupeKey);
            if (lastTime != null && (now - lastTime) < 2000) {
                log.debug("[AUDITORIA DUP] Registro duplicado ignorado (<2s): {}", dedupeKey);
                return;
            }
            RECENT_LOGS.put(dedupeKey, now);

            LogAuditoria logEntry = LogAuditoria.builder()
                    .usuarioEmail(usuarioEmail)
                    .usuarioNome(usuarioNome)
                    .acao(acao)
                    .recurso(recurso)
                    .recursoId(recursoId)
                    .detalhes(detalhes)
                    .criadoEm(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))
                    .build();

            repository.save(logEntry);
            log.info("[AUDITORIA] [{}] [{}] por {}: {}", acao, recurso, usuarioEmail, detalhes);
        } catch (Exception e) {
            log.error("Falha ao registrar log de auditoria: {}", e.getMessage(), e);
        }
    }
}
