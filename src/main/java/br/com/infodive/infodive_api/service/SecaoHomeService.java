package br.com.infodive.infodive_api.service;

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
    public SecaoHomeResponse findBySecao(String secao) {
        return secaoHomeRepository.findBySecao(secao)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Seção da home não encontrada: " + secao));
    }

    private SecaoHomeResponse toResponse(SecaoHome e) {
        return new SecaoHomeResponse(e.getSecao(), e.getEyebrow(), e.getHeadline(), e.getSubtitulo());
    }
}
