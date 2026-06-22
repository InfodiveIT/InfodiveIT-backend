package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.response.HomeSegurancaMarqueeResponse;
import br.com.infodive.infodive_api.entity.HomeSegurancaMarquee;
import br.com.infodive.infodive_api.repository.HomeSegurancaMarqueeRepository;
import java.util.List;
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

    private HomeSegurancaMarqueeResponse toResponse(HomeSegurancaMarquee e) {
        return new HomeSegurancaMarqueeResponse(e.getId(), e.getIcone(), e.getTitulo(), e.getCorpo(), e.getOrdem());
    }
}
