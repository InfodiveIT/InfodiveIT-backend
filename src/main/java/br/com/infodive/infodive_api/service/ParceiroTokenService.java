package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.ParceiroTokenRequest;
import br.com.infodive.infodive_api.dto.response.ParceiroTokenResponse;
import br.com.infodive.infodive_api.entity.ParceiroToken;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.ParceiroTokenRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParceiroTokenService {

    private final ParceiroTokenRepository repository;

    @Transactional(readOnly = true)
    public List<ParceiroTokenResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ParceiroTokenResponse findById(UUID id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Token de parceiro não encontrado: " + id));
    }

    @Transactional
    public ParceiroTokenResponse create(ParceiroTokenRequest request, String criadoPor) {
        String tokenStr = "infodive_pat_" + UUID.randomUUID().toString().replace("-", "");

        LocalDateTime expiraEm = null;
        if (request.diasValidade() != null && request.diasValidade() > 0) {
            expiraEm = LocalDateTime.now().plusDays(request.diasValidade());
        }

        String role = (request.role() != null && !request.role().isBlank()) ? request.role().trim() : "ROLE_BLOGGER";
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        ParceiroToken entity = ParceiroToken.builder()
                .nomeAgencia(request.nomeAgencia().trim())
                .email(request.email().trim())
                .token(tokenStr)
                .role(role)
                .expiraEm(expiraEm)
                .ativo(true)
                .criadoPor(criadoPor != null ? criadoPor : "sistema")
                .build();

        ParceiroToken saved = repository.save(entity);
        log.info("Novo Token de Parceiro criado para [{}] ({}) com validade até: {}", saved.getNomeAgencia(), saved.getEmail(), saved.getExpiraEm());
        return toResponse(saved);
    }

    @Transactional
    public ParceiroTokenResponse revogar(UUID id) {
        ParceiroToken entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Token de parceiro não encontrado: " + id));
        entity.setAtivo(false);
        log.info("Token de Parceiro revogado para [{}] ({})", entity.getNomeAgencia(), entity.getEmail());
        return toResponse(repository.save(entity));
    }

    @Transactional
    public ParceiroTokenResponse renovar(UUID id, int dias) {
        ParceiroToken entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Token de parceiro não encontrado: " + id));
        
        int diasAdicionar = dias > 0 ? dias : 30;
        entity.setExpiraEm(LocalDateTime.now().plusDays(diasAdicionar));
        entity.setAtivo(true);
        log.info("Token de Parceiro renovado por {} dias para [{}]", diasAdicionar, entity.getNomeAgencia());
        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        ParceiroToken entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Token de parceiro não encontrado: " + id));
        repository.delete(entity);
    }

    @Transactional(readOnly = true)
    public Optional<ParceiroToken> validarToken(String tokenStr) {
        if (tokenStr == null || tokenStr.isBlank()) {
            return Optional.empty();
        }
        String cleanToken = tokenStr.trim();
        Optional<ParceiroToken> opt = repository.findByTokenAndAtivoTrue(cleanToken);
        if (opt.isPresent()) {
            ParceiroToken token = opt.get();
            if (token.getExpiraEm() != null && token.getExpiraEm().isBefore(LocalDateTime.now())) {
                log.warn("Tentativa de uso de Token de Parceiro expirado para [{}]: {}", token.getEmail(), cleanToken);
                return Optional.empty();
            }
            return Optional.of(token);
        }
        return Optional.empty();
    }

    public ParceiroTokenResponse toResponse(ParceiroToken entity) {
        boolean expirado = entity.getExpiraEm() != null && entity.getExpiraEm().isBefore(LocalDateTime.now());
        return new ParceiroTokenResponse(
                entity.getId(),
                entity.getNomeAgencia(),
                entity.getEmail(),
                entity.getToken(),
                entity.getRole(),
                entity.getExpiraEm(),
                entity.isAtivo(),
                expirado,
                entity.getCriadoPor(),
                entity.getCriadoEm(),
                entity.getAtualizadoEm()
        );
    }
}
