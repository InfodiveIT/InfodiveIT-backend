package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.HomeProblemasRequest;
import br.com.infodive.infodive_api.dto.response.HomeProblemasResponse;
import br.com.infodive.infodive_api.entity.HomeProblemas;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.HomeProblemasRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeProblemasService {

    private final HomeProblemasRepository repository;

    @Transactional(readOnly = true)
    public List<HomeProblemasResponse> findAll() {
        return repository.findAllByAtivoTrueOrderByOrdemAsc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public HomeProblemasResponse create(HomeProblemasRequest request) {
        HomeProblemas entity = HomeProblemas.builder()
                .titulo(request.titulo())
                .descricao(request.descricao())
                .solucaoIndicada(request.solucaoIndicada())
                .href(request.href())
                .ordem(request.ordem())
                .build();
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public HomeProblemasResponse findById(UUID id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Problema não encontrado: " + id));
    }

    @Transactional
    public HomeProblemasResponse update(UUID id, HomeProblemasRequest request) {
        HomeProblemas entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problema não encontrado: " + id));
        entity.setTitulo(request.titulo());
        entity.setDescricao(request.descricao());
        entity.setSolucaoIndicada(request.solucaoIndicada());
        entity.setHref(request.href());
        entity.setOrdem(request.ordem());
        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        HomeProblemas entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problema não encontrado: " + id));
        entity.setAtivo(false);
        repository.save(entity);
    }

    private HomeProblemasResponse toResponse(HomeProblemas e) {
        return new HomeProblemasResponse(
                e.getId(), e.getTitulo(), e.getDescricao(), e.getSolucaoIndicada(), e.getHref(), e.getOrdem());
    }
}
