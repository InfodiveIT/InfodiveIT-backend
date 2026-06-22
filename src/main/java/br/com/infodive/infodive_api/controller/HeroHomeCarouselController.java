package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.response.HeroHomeCarouselResponse;
import br.com.infodive.infodive_api.service.HeroHomeCarouselService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hero-carousel")
@RequiredArgsConstructor
public class HeroHomeCarouselController {

    private final HeroHomeCarouselService heroHomeCarouselService;

    @GetMapping
    public ResponseEntity<List<HeroHomeCarouselResponse>> findAll() {
        return ResponseEntity.ok(heroHomeCarouselService.findAll());
    }
}
