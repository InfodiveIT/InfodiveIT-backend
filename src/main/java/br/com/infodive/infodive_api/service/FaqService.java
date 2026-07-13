package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.FaqRequest;
import br.com.infodive.infodive_api.dto.response.FaqResponse;
import br.com.infodive.infodive_api.entity.Faq;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.FaqRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    @Transactional(readOnly = true)
    public List<FaqResponse> findAll() {
        return faqRepository.findAllByAtivoTrueOrderByOrdemAsc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public FaqResponse create(FaqRequest request) {
        Faq entity = Faq.builder()
                .pergunta(request.pergunta())
                .resposta(request.resposta())
                .ordem(request.ordem())
                .build();
        return toResponse(faqRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public FaqResponse findById(UUID id) {
        return faqRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ não encontrado: " + id));
    }

    @Transactional
    public FaqResponse update(UUID id, FaqRequest request) {
        Faq entity = faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ não encontrado: " + id));
        entity.setPergunta(request.pergunta());
        entity.setResposta(request.resposta());
        entity.setOrdem(request.ordem());
        return toResponse(faqRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        Faq entity = faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ não encontrado: " + id));
        entity.setAtivo(false);
        faqRepository.save(entity);
    }

    private FaqResponse toResponse(Faq e) {
        return new FaqResponse(e.getId(), e.getPergunta(), e.getResposta(), e.getOrdem());
    }
}
