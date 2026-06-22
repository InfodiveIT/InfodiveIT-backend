package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.response.HomeSolucoesBentoResponse;
import br.com.infodive.infodive_api.entity.HomeSolucoesBento;
import br.com.infodive.infodive_api.repository.HomeSolucoesBentoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeSolucoesBentoService {

    private final HomeSolucoesBentoRepository repository;

    @Transactional(readOnly = true)
    public List<HomeSolucoesBentoResponse> findAll() {
        return repository.findAllByOrderByOrdemAsc().stream().map(this::toResponse).toList();
    }

    private HomeSolucoesBentoResponse toResponse(HomeSolucoesBento e) {
        return new HomeSolucoesBentoResponse(
                e.getId(), e.getNome(), e.getDescricao(), e.getIcone(), e.getImagemIaUrl(), e.getOrdem());
    }
}
