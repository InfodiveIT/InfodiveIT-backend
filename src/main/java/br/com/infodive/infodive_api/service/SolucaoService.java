package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.SolucaoRequest;
import br.com.infodive.infodive_api.dto.response.SolucaoResponse;
import br.com.infodive.infodive_api.entity.Solucao;
import br.com.infodive.infodive_api.entity.Categoria;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.SolucaoMapper;
import br.com.infodive.infodive_api.repository.CategoriaRepository;
import br.com.infodive.infodive_api.repository.FabricanteRepository;
import br.com.infodive.infodive_api.repository.SolucaoRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SolucaoService {

    private final SolucaoRepository solucaoRepository;
    private final FabricanteRepository fabricanteRepository;
    private final CategoriaRepository categoriaRepository;
    private final SolucaoMapper solucaoMapper;
    private final SupabaseStorageService supabaseStorageService;

    @Transactional(readOnly = true)
    public List<SolucaoResponse> findAll() {
        return solucaoRepository.findAllByAtivoTrueOrderByOrdemAscTituloAsc()
                .stream()
                .map(solucaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SolucaoResponse findBySlug(String slug) {
        return solucaoRepository.findBySlugAndAtivoTrue(slug)
                .map(solucaoMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + slug));
    }

    @Transactional(readOnly = true)
    public SolucaoResponse findById(UUID id) {
        return solucaoRepository.findById(id)
                .map(solucaoMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));
    }

    @Transactional
    public SolucaoResponse create(SolucaoRequest request) {
        Solucao solucao = solucaoMapper.toEntity(request);
        solucao.setFabricantes(resolveFabricantes(request.fabricanteIds()));
        solucao.setCategoria(resolveCategoria(request.categoriaId()));
        return solucaoMapper.toResponse(solucaoRepository.save(solucao));
    }

    @Transactional
    public SolucaoResponse update(UUID id, SolucaoRequest request) {
        Solucao solucao = solucaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));
        
        String oldImagemUrl = solucao.getImagemUrl();
        solucaoMapper.updateEntity(solucao, request);
        
        String newImagemUrl = solucao.getImagemUrl();
        if (oldImagemUrl != null && !oldImagemUrl.equals(newImagemUrl)) {
            supabaseStorageService.deleteFile(oldImagemUrl);
        }

        solucao.setFabricantes(resolveFabricantes(request.fabricanteIds()));
        solucao.setCategoria(resolveCategoria(request.categoriaId()));
        return solucaoMapper.toResponse(solucaoRepository.save(solucao));
    }

    @Transactional
    public void delete(UUID id) {
        Solucao solucao = solucaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solução não encontrada: " + id));
        if (solucao.getImagemUrl() != null) {
            supabaseStorageService.deleteFile(solucao.getImagemUrl());
        }
        solucaoRepository.delete(solucao);
    }

    private List<br.com.infodive.infodive_api.entity.Fabricante> resolveFabricantes(List<UUID> fabricanteIds) {
        if (fabricanteIds == null || fabricanteIds.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return fabricanteRepository.findAllById(fabricanteIds);
    }

    private Categoria resolveCategoria(UUID categoriaId) {
        if (categoriaId == null) {
            return null;
        }
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + categoriaId));
    }
}
