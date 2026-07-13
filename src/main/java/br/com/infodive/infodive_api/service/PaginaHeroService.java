package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.PaginaHeroRequest;
import br.com.infodive.infodive_api.dto.response.PaginaHeroResponse;
import br.com.infodive.infodive_api.entity.PaginaHero;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.PaginaHeroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaginaHeroService {

    private final PaginaHeroRepository paginaHeroRepository;

    @Transactional(readOnly = true)
    public java.util.List<PaginaHeroResponse> findAll() {
        return paginaHeroRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PaginaHeroResponse findByIdentifier(String identifier) {
        try {
            java.util.UUID id = java.util.UUID.fromString(identifier);
            return paginaHeroRepository.findById(id)
                    .map(this::toResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("Hero não encontrado para o ID: " + id));
        } catch (IllegalArgumentException e) {
            return paginaHeroRepository.findByPagina(identifier)
                    .map(this::toResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("Hero não encontrado para a página: " + identifier));
        }
    }

    @Transactional
    public PaginaHeroResponse update(String identifier, PaginaHeroRequest request) {
        PaginaHero entity;
        try {
            java.util.UUID id = java.util.UUID.fromString(identifier);
            entity = paginaHeroRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Hero não encontrado para o ID: " + id));
        } catch (IllegalArgumentException e) {
            entity = paginaHeroRepository.findByPagina(identifier)
                    .orElseThrow(() -> new ResourceNotFoundException("Hero não encontrado para a página: " + identifier));
        }
        entity.setEyebrow(request.eyebrow());
        entity.setHeadline(request.headline());
        entity.setHeadlineDestaque(request.headlineDestaque());
        entity.setSubtitulo(request.subtitulo());
        entity.setTagline(request.tagline());
        return toResponse(paginaHeroRepository.save(entity));
    }

    private PaginaHeroResponse toResponse(PaginaHero e) {
        return new PaginaHeroResponse(
                e.getId(),
                e.getPagina(),
                e.getEyebrow(),
                e.getHeadline(),
                e.getHeadlineDestaque(),
                e.getSubtitulo(),
                e.getTagline()
        );
    }

}
