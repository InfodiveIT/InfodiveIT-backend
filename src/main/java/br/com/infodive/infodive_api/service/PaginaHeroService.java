package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.response.PaginaHeroResponse;
import br.com.infodive.infodive_api.entity.PaginaHero;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.PaginaHeroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaginaHeroService {

    private final PaginaHeroRepository paginaHeroRepository;

    @Transactional(readOnly = true)
    public PaginaHeroResponse findByPagina(String pagina) {
        return paginaHeroRepository.findByPagina(pagina)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Hero não encontrado para a página: " + pagina));
    }

    private PaginaHeroResponse toResponse(PaginaHero e) {
        return new PaginaHeroResponse(e.getPagina(), e.getEyebrow(), e.getHeadline(), e.getSubtitulo(), e.getTagline());
    }
}
