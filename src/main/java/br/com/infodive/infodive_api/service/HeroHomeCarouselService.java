package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.response.HeroHomeCarouselResponse;
import br.com.infodive.infodive_api.entity.HeroHomeCarousel;
import br.com.infodive.infodive_api.repository.HeroHomeCarouselRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HeroHomeCarouselService {

    private final HeroHomeCarouselRepository heroHomeCarouselRepository;

    @Transactional(readOnly = true)
    public List<HeroHomeCarouselResponse> findAll() {
        return heroHomeCarouselRepository.findAllByAtivoTrueOrderByOrdemAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private HeroHomeCarouselResponse toResponse(HeroHomeCarousel e) {
        return new HeroHomeCarouselResponse(e.getId(), e.getImagemUrl(), e.getOrdem());
    }
}
