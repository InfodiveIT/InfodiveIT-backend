package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.CtaRequest;
import br.com.infodive.infodive_api.dto.response.CtaResponse;
import br.com.infodive.infodive_api.entity.Cta;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.CtaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CtaService {

    private final CtaRepository ctaRepository;

    @Transactional(readOnly = true)
    public java.util.List<CtaResponse> findAll() {
        return ctaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CtaResponse findByIdentifier(String identifier) {
        try {
            java.util.UUID id = java.util.UUID.fromString(identifier);
            return ctaRepository.findById(id)
                    .map(this::toResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("CTA não encontrado para o ID: " + id));
        } catch (IllegalArgumentException e) {
            return ctaRepository.findByPagina(identifier)
                    .map(this::toResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("CTA não encontrado para a página: " + identifier));
        }
    }

    @Transactional
    public CtaResponse update(String identifier, CtaRequest request) {
        Cta entity;
        try {
            java.util.UUID id = java.util.UUID.fromString(identifier);
            entity = ctaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("CTA não encontrado para o ID: " + id));
        } catch (IllegalArgumentException e) {
            entity = ctaRepository.findByPagina(identifier)
                    .orElseThrow(() -> new ResourceNotFoundException("CTA não encontrado para a página: " + identifier));
        }
        entity.setTitulo(request.titulo());
        entity.setSubtitulo(request.subtitulo());
        entity.setCtaTexto(request.ctaTexto());
        return toResponse(ctaRepository.save(entity));
    }

    private CtaResponse toResponse(Cta e) {
        return new CtaResponse(e.getId(), e.getPagina(), e.getTitulo(), e.getSubtitulo(), e.getCtaTexto());
    }

}
