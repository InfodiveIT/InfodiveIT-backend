package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.response.PaginaHeroResponse;
import br.com.infodive.infodive_api.service.PaginaHeroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paginas-hero")
@RequiredArgsConstructor
public class PaginaHeroController {

    private final PaginaHeroService paginaHeroService;

    @GetMapping("/{pagina}")
    public ResponseEntity<PaginaHeroResponse> findByPagina(@PathVariable String pagina) {
        return ResponseEntity.ok(paginaHeroService.findByPagina(pagina));
    }
}
