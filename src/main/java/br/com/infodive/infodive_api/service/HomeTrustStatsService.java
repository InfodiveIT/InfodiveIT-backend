package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.HomeTrustStatsRequest;
import br.com.infodive.infodive_api.dto.response.HomeTrustStatsResponse;
import br.com.infodive.infodive_api.entity.HomeTrustStats;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.HomeTrustStatsRepository;
import java.util.List;
import java.util.UUID;
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

    @Transactional
    public HomeTrustStatsResponse create(HomeTrustStatsRequest request) {
        HomeTrustStats entity = HomeTrustStats.builder()
                .eyebrow(request.eyebrow())
                .prefixo(request.prefixo())
                .valor(request.valor())
                .valorInicial(request.valorInicial())
                .sufixo(request.sufixo())
                .titulo(request.titulo())
                .descricao(request.descricao())
                .ordem(request.ordem())
                .build();
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public HomeTrustStatsResponse findById(UUID id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Stat não encontrado: " + id));
    }

    @Transactional
    public HomeTrustStatsResponse update(UUID id, HomeTrustStatsRequest request) {
        HomeTrustStats entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stat não encontrado: " + id));
        entity.setEyebrow(request.eyebrow());
        entity.setPrefixo(request.prefixo());
        entity.setValor(request.valor());
        entity.setValorInicial(request.valorInicial());
        entity.setSufixo(request.sufixo());
        entity.setTitulo(request.titulo());
        entity.setDescricao(request.descricao());
        entity.setOrdem(request.ordem());
        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Stat não encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private HomeTrustStatsResponse toResponse(HomeTrustStats e) {
        return new HomeTrustStatsResponse(
                e.getId(), e.getEyebrow(), e.getPrefixo(), e.getValor(), e.getValorInicial(),
                e.getSufixo(), e.getTitulo(), e.getDescricao(), e.getOrdem());
    }
}
