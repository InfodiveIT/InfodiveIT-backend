package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.HeroHomeCarouselRequest;
import br.com.infodive.infodive_api.dto.response.HeroHomeCarouselResponse;
import br.com.infodive.infodive_api.entity.HeroHomeCarousel;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.HeroHomeCarouselRepository;
import java.util.List;
import java.util.UUID;
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

    @Transactional
    public HeroHomeCarouselResponse create(HeroHomeCarouselRequest request) {
        HeroHomeCarousel entity = HeroHomeCarousel.builder()
                .imagemUrl(request.imagemUrl())
                .ordem(request.ordem())
                .build();
        return toResponse(heroHomeCarouselRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public HeroHomeCarouselResponse findById(UUID id) {
        return heroHomeCarouselRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Item do carousel não encontrado: " + id));
    }

    @Transactional
    public HeroHomeCarouselResponse update(UUID id, HeroHomeCarouselRequest request) {
        HeroHomeCarousel entity = heroHomeCarouselRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item do carousel não encontrado: " + id));
        entity.setImagemUrl(request.imagemUrl());
        entity.setOrdem(request.ordem());
        return toResponse(heroHomeCarouselRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        HeroHomeCarousel entity = heroHomeCarouselRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item do carousel não encontrado: " + id));
        entity.setAtivo(false);
        heroHomeCarouselRepository.save(entity);
    }

    private HeroHomeCarouselResponse toResponse(HeroHomeCarousel e) {
        return new HeroHomeCarouselResponse(e.getId(), e.getImagemUrl(), e.getOrdem());
    }
}
