package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.response.FaqResponse;
import br.com.infodive.infodive_api.entity.Faq;
import br.com.infodive.infodive_api.repository.FaqRepository;
import java.util.List;
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

    private FaqResponse toResponse(Faq e) {
        return new FaqResponse(e.getId(), e.getPergunta(), e.getResposta(), e.getOrdem());
    }
}
