package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.SobreCulturaRequest;
import br.com.infodive.infodive_api.dto.response.SobreCulturaResponse;
import br.com.infodive.infodive_api.entity.SobreCultura;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.SobreCulturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SobreCulturaService {

    private final SobreCulturaRepository repository;
    private final SupabaseStorageService supabaseStorageService;

    @Transactional(readOnly = true)
    public SobreCulturaResponse get() {
        return repository.findAll().stream().findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Cultura (sobre) não encontrada"));
    }

    @Transactional
    public SobreCulturaResponse update(SobreCulturaRequest request) {
        SobreCultura entity = repository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cultura (sobre) não encontrada"));
        
        var oldFotos = entity.getFotos();

        entity.setEyebrow(request.eyebrow());
        entity.setHeadline(request.headline());
        entity.setParagrafo(request.paragrafo());
        entity.setFotos(request.fotos());

        if (oldFotos != null && request.fotos() != null) {
            java.util.Set<String> newUrls = request.fotos().stream()
                    .map(f -> f.imagemUrl())
                    .filter(java.util.Objects::nonNull)
                    .collect(java.util.stream.Collectors.toSet());
            for (var oldFoto : oldFotos) {
                if (oldFoto.imagemUrl() != null && !newUrls.contains(oldFoto.imagemUrl())) {
                    supabaseStorageService.deleteFile(oldFoto.imagemUrl());
                }
            }
        }

        return toResponse(repository.save(entity));
    }

    private SobreCulturaResponse toResponse(SobreCultura e) {
        return new SobreCulturaResponse(e.getEyebrow(), e.getHeadline(), e.getParagrafo(), e.getFotos());
    }
}
