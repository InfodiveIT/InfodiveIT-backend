package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.PaginaHeroRequest;
import br.com.infodive.infodive_api.dto.response.PaginaHeroResponse;
import br.com.infodive.infodive_api.service.PaginaHeroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paginas-hero")
@RequiredArgsConstructor
public class PaginaHeroController {

    private final PaginaHeroService paginaHeroService;

    @GetMapping
    public ResponseEntity<java.util.List<PaginaHeroResponse>> findAll() {
        return ResponseEntity.ok(paginaHeroService.findAll());
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<PaginaHeroResponse> findByIdentifier(@PathVariable String identifier) {
        return ResponseEntity.ok(paginaHeroService.findByIdentifier(identifier));
    }

    @PutMapping("/{identifier}")
    public ResponseEntity<PaginaHeroResponse> update(
            @PathVariable String identifier, @Valid @RequestBody PaginaHeroRequest request) {
        return ResponseEntity.ok(paginaHeroService.update(identifier, request));
    }

}
