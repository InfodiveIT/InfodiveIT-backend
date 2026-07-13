package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.SecaoHomeRequest;
import br.com.infodive.infodive_api.dto.response.SecaoHomeResponse;
import br.com.infodive.infodive_api.entity.SecaoHome;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.SecaoHomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SecaoHomeService {

    private final SecaoHomeRepository secaoHomeRepository;

    @Transactional(readOnly = true)
    public java.util.List<SecaoHomeResponse> findAll() {
        return secaoHomeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SecaoHomeResponse findByIdentifier(String identifier) {
        try {
            java.util.UUID id = java.util.UUID.fromString(identifier);
            return secaoHomeRepository.findById(id)
                    .map(this::toResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("Seção da home não encontrada para o ID: " + id));
        } catch (IllegalArgumentException e) {
            return secaoHomeRepository.findBySecao(identifier)
                    .map(this::toResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("Seção da home não encontrada: " + identifier));
        }
    }

    @Transactional
    public SecaoHomeResponse update(String identifier, SecaoHomeRequest request) {
        SecaoHome entity;
        try {
            java.util.UUID id = java.util.UUID.fromString(identifier);
            entity = secaoHomeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Seção da home não encontrada para o ID: " + id));
        } catch (IllegalArgumentException e) {
            entity = secaoHomeRepository.findBySecao(identifier)
                    .orElseThrow(() -> new ResourceNotFoundException("Seção da home não encontrada: " + identifier));
        }
        entity.setEyebrow(request.eyebrow());
        entity.setHeadline(request.headline());
        entity.setHeadlineDestaque(request.headlineDestaque());
        entity.setSubtitulo(request.subtitulo());
        entity.setBoxTitulo(request.boxTitulo());
        entity.setBoxDescricao(request.boxDescricao());
        return toResponse(secaoHomeRepository.save(entity));
    }

    private SecaoHomeResponse toResponse(SecaoHome e) {
        return new SecaoHomeResponse(
                e.getId(),
                e.getSecao(),
                e.getEyebrow(),
                e.getHeadline(),
                e.getHeadlineDestaque(),
                e.getSubtitulo(),
                e.getBoxTitulo(),
                e.getBoxDescricao()
        );
    }

}
