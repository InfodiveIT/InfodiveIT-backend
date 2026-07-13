package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.HeroHomeCarouselRequest;
import br.com.infodive.infodive_api.dto.response.HeroHomeCarouselResponse;
import br.com.infodive.infodive_api.service.HeroHomeCarouselService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/{id}")
    public ResponseEntity<HeroHomeCarouselResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(heroHomeCarouselService.findById(id));
    }

    @PostMapping
    public ResponseEntity<HeroHomeCarouselResponse> create(@Valid @RequestBody HeroHomeCarouselRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(heroHomeCarouselService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HeroHomeCarouselResponse> update(
            @PathVariable UUID id, @Valid @RequestBody HeroHomeCarouselRequest request) {
        return ResponseEntity.ok(heroHomeCarouselService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        heroHomeCarouselService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
