package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.ServicoRequest;
import br.com.infodive.infodive_api.dto.response.ServicoResponse;
import br.com.infodive.infodive_api.entity.Servico;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.ServicoMapper;
import br.com.infodive.infodive_api.repository.ServicoRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final ServicoMapper servicoMapper;

    @Transactional(readOnly = true)
    public List<ServicoResponse> findAll() {
        return servicoRepository.findAllByAtivoTrueOrderByOrdemAscNomeAsc()
                .stream()
                .map(servicoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServicoResponse findBySlug(String slug) {
        return servicoRepository.findBySlugAndAtivoTrue(slug)
                .map(servicoMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado: " + slug));
    }

    @Transactional(readOnly = true)
    public ServicoResponse findById(UUID id) {
        return servicoRepository.findById(id)
                .map(servicoMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado: " + id));
    }

    @Transactional
    public ServicoResponse create(ServicoRequest request) {
        Servico servico = servicoMapper.toEntity(request);
        return servicoMapper.toResponse(servicoRepository.save(servico));
    }

    @Transactional
    public ServicoResponse update(UUID id, ServicoRequest request) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado: " + id));
        servicoMapper.updateEntity(servico, request);
        return servicoMapper.toResponse(servicoRepository.save(servico));
    }

    @Transactional
    public void delete(UUID id) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado: " + id));
        servico.setAtivo(false);
        servicoRepository.save(servico);
    }
}
