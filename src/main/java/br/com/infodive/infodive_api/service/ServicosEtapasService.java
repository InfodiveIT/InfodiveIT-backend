package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.response.ServicosEtapasResponse;
import br.com.infodive.infodive_api.entity.ServicosEtapas;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.ServicosEtapasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServicosEtapasService {

    private final ServicosEtapasRepository repository;

    @Transactional(readOnly = true)
    public ServicosEtapasResponse get() {
        return repository.findAll().stream().findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Etapas de serviços não encontradas"));
    }

    private ServicosEtapasResponse toResponse(ServicosEtapas e) {
        return new ServicosEtapasResponse(e.getEyebrow(), e.getHeadline(), e.getSubtitulo(), e.getEtapas());
    }
}
