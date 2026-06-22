package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.response.ContatoInfoResponse;
import br.com.infodive.infodive_api.entity.ContatoInfo;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.ContatoInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContatoInfoService {

    private final ContatoInfoRepository contatoInfoRepository;

    @Transactional(readOnly = true)
    public ContatoInfoResponse get() {
        return contatoInfoRepository.findAll().stream().findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Informações de contato não encontradas"));
    }

    private ContatoInfoResponse toResponse(ContatoInfo e) {
        return new ContatoInfoResponse(
                e.getEyebrow(), e.getHeadline(), e.getSubtitulo(), e.getEmail(), e.getTelefone(), e.getEndereco(),
                e.getHorarioComercial(), e.getHorarioNoc(), e.getCardTitulo(), e.getCardDescricao(),
                e.getCardBullets(), e.getCardCtaTexto(), e.getCardStatus());
    }
}
