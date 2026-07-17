package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.CategoriaRequest;
import br.com.infodive.infodive_api.dto.response.CategoriaResponse;
import br.com.infodive.infodive_api.entity.Categoria;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.CategoriaMapper;
import br.com.infodive.infodive_api.repository.CategoriaRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;
    private final CategoriaMapper mapper;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> findAll() {
        return repository.findAllByAtivoTrueOrderByOrdemAscNomeAsc()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse findBySlug(String slug) {
        return repository.findBySlugAndAtivoTrue(slug)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + slug));
    }

    @Transactional(readOnly = true)
    public CategoriaResponse findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));
    }

    @Transactional
    public CategoriaResponse create(CategoriaRequest request) {
        Categoria entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public CategoriaResponse update(UUID id, CategoriaRequest request) {
        Categoria entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));
        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        Categoria entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));
        repository.delete(entity);
    }
}
