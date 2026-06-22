package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.response.SobreValoresResponse;
import br.com.infodive.infodive_api.entity.SobreValores;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.SobreValoresRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SobreValoresService {

    private final SobreValoresRepository repository;

    @Transactional(readOnly = true)
    public SobreValoresResponse get() {
        return repository.findAll().stream().findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Valores (sobre) não encontrados"));
    }

    private SobreValoresResponse toResponse(SobreValores e) {
        return new SobreValoresResponse(e.getEyebrow(), e.getHeadline(), e.getParagrafo(), e.getValores());
    }
}
