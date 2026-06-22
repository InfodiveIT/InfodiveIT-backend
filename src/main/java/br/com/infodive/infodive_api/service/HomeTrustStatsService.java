package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.response.HomeTrustStatsResponse;
import br.com.infodive.infodive_api.entity.HomeTrustStats;
import br.com.infodive.infodive_api.repository.HomeTrustStatsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeTrustStatsService {

    private final HomeTrustStatsRepository repository;

    @Transactional(readOnly = true)
    public List<HomeTrustStatsResponse> findAll() {
        return repository.findAllByOrderByOrdemAsc().stream().map(this::toResponse).toList();
    }

    private HomeTrustStatsResponse toResponse(HomeTrustStats e) {
        return new HomeTrustStatsResponse(
                e.getId(), e.getEyebrow(), e.getPrefixo(), e.getValor(), e.getValorInicial(),
                e.getSufixo(), e.getTitulo(), e.getDescricao(), e.getOrdem());
    }
}
