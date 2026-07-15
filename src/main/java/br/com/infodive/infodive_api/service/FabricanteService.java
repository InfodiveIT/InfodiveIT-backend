package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.FabricanteRequest;
import br.com.infodive.infodive_api.dto.response.FabricanteResponse;
import br.com.infodive.infodive_api.entity.Fabricante;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.FabricanteMapper;
import br.com.infodive.infodive_api.repository.FabricanteRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FabricanteService {

    private final FabricanteRepository fabricanteRepository;
    private final FabricanteMapper fabricanteMapper;
    private final SupabaseStorageService supabaseStorageService;

    @Transactional(readOnly = true)
    public List<FabricanteResponse> findAll(Boolean destaque) {
        return fabricanteRepository.findAllWithFilters(destaque)
                .stream()
                .map(fabricanteMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FabricanteResponse findBySlug(String slug) {
        return fabricanteRepository.findBySlugAndAtivoTrue(slug)
                .map(fabricanteMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Fabricante não encontrado: " + slug));
    }

    @Transactional(readOnly = true)
    public FabricanteResponse findById(UUID id) {
        return fabricanteRepository.findById(id)
                .map(fabricanteMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Fabricante não encontrado: " + id));
    }

    @Transactional
    public FabricanteResponse create(FabricanteRequest request) {
        Fabricante fabricante = fabricanteMapper.toEntity(request);
        return fabricanteMapper.toResponse(fabricanteRepository.save(fabricante));
    }

    @Transactional
    public FabricanteResponse update(UUID id, FabricanteRequest request) {
        Fabricante fabricante = fabricanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fabricante não encontrado: " + id));
        
        String oldLogoUrl = fabricante.getLogoUrl();
        fabricanteMapper.updateEntity(fabricante, request);
        
        String newLogoUrl = fabricante.getLogoUrl();
        if (oldLogoUrl != null && !oldLogoUrl.equals(newLogoUrl)) {
            supabaseStorageService.deleteFile(oldLogoUrl);
        }

        return fabricanteMapper.toResponse(fabricanteRepository.save(fabricante));
    }

    @Transactional
    public void delete(UUID id) {
        Fabricante fabricante = fabricanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fabricante não encontrado: " + id));
        fabricante.setAtivo(false); // soft delete
        fabricanteRepository.save(fabricante);
    }
}
