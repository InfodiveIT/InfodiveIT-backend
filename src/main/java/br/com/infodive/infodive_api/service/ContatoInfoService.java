package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.ContatoInfoRequest;
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

    @Transactional
    public ContatoInfoResponse update(ContatoInfoRequest request) {
        ContatoInfo entity = contatoInfoRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Informações de contato não encontradas"));
        entity.setEyebrow(request.eyebrow());
        entity.setHeadline(request.headline());
        entity.setHeadlineDestaque(request.headlineDestaque());
        entity.setSubtitulo(request.subtitulo());
        entity.setEmail(request.email());
        entity.setTelefone(request.telefone());
        entity.setEndereco(request.endereco());
        entity.setHorarioComercial(request.horarioComercial());
        entity.setHorarioNoc(request.horarioNoc());
        entity.setCardTitulo(request.cardTitulo());
        entity.setCardDescricao(request.cardDescricao());
        entity.setCardBullets(request.cardBullets());
        entity.setCardCtaTexto(request.cardCtaTexto());
        entity.setCardStatus(request.cardStatus());
        return toResponse(contatoInfoRepository.save(entity));
    }

    private ContatoInfoResponse toResponse(ContatoInfo e) {
        return new ContatoInfoResponse(
                e.getEyebrow(), e.getHeadline(), e.getHeadlineDestaque(), e.getSubtitulo(), e.getEmail(), e.getTelefone(), e.getEndereco(),
                e.getHorarioComercial(), e.getHorarioNoc(), e.getCardTitulo(), e.getCardDescricao(),
                e.getCardBullets(), e.getCardCtaTexto(), e.getCardStatus());
    }
}
