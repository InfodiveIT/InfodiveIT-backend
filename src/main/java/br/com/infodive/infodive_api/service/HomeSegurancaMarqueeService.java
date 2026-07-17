package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.HomeSegurancaMarqueeRequest;
import br.com.infodive.infodive_api.dto.response.HomeSegurancaMarqueeResponse;
import br.com.infodive.infodive_api.entity.HomeSegurancaMarquee;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.HomeSegurancaMarqueeRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeSegurancaMarqueeService {

    private final HomeSegurancaMarqueeRepository repository;

    @Transactional(readOnly = true)
    public List<HomeSegurancaMarqueeResponse> findAll() {
        return repository.findAllByAtivoTrueOrderByOrdemAsc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public HomeSegurancaMarqueeResponse create(HomeSegurancaMarqueeRequest request) {
        HomeSegurancaMarquee entity = HomeSegurancaMarquee.builder()
                .icone(request.icone())
                .titulo(request.titulo())
                .corpo(request.corpo())
                .ordem(request.ordem())
                .build();
        return toResponse(repository.save(entity));
    }

    @Transactional
    public HomeSegurancaMarqueeResponse update(UUID id, HomeSegurancaMarqueeRequest request) {
        HomeSegurancaMarquee entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item do marquee não encontrado: " + id));
        entity.setIcone(request.icone());
        entity.setTitulo(request.titulo());
        entity.setCorpo(request.corpo());
        entity.setOrdem(request.ordem());
        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        HomeSegurancaMarquee entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item do marquee não encontrado: " + id));
        repository.delete(entity);
    }

    private HomeSegurancaMarqueeResponse toResponse(HomeSegurancaMarquee e) {
        return new HomeSegurancaMarqueeResponse(e.getId(), e.getIcone(), e.getTitulo(), e.getCorpo(), e.getOrdem());
    }
}
