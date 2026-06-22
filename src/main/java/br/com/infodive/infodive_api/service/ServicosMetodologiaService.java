package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.response.ServicosMetodologiaResponse;
import br.com.infodive.infodive_api.entity.ServicosMetodologia;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.ServicosMetodologiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServicosMetodologiaService {

    private final ServicosMetodologiaRepository repository;

    @Transactional(readOnly = true)
    public ServicosMetodologiaResponse get() {
        return repository.findAll().stream().findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Metodologia de serviços não encontrada"));
    }

    private ServicosMetodologiaResponse toResponse(ServicosMetodologia e) {
        return new ServicosMetodologiaResponse(
                e.getEyebrow(), e.getHeadline(), e.getParagrafo(), e.getMetricas(), e.getPilares());
    }
}
