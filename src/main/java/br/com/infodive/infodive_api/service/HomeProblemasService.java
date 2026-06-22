package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.response.HomeProblemasResponse;
import br.com.infodive.infodive_api.entity.HomeProblemas;
import br.com.infodive.infodive_api.repository.HomeProblemasRepository;
import java.util.List;
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

    private HomeProblemasResponse toResponse(HomeProblemas e) {
        return new HomeProblemasResponse(
                e.getId(), e.getTitulo(), e.getDescricao(), e.getSolucaoIndicada(), e.getHref(), e.getOrdem());
    }
}
