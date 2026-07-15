package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.CaseRequest;
import br.com.infodive.infodive_api.dto.response.CaseResponse;
import br.com.infodive.infodive_api.entity.Case;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.CaseMapper;
import br.com.infodive.infodive_api.repository.CaseRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;
    private final CaseMapper caseMapper;
    private final SupabaseStorageService supabaseStorageService;

    @Transactional(readOnly = true)
    public List<CaseResponse> findAll() {
        return caseRepository.findAllByAtivoTrueOrderByOrdemAsc()
                .stream()
                .map(caseMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CaseResponse findById(UUID id) {
        return caseRepository.findById(id)
                .map(caseMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Case não encontrado: " + id));
    }

    @Transactional
    public CaseResponse create(CaseRequest request) {
        Case entity = caseMapper.toEntity(request);
        return caseMapper.toResponse(caseRepository.save(entity));
    }

    @Transactional
    public CaseResponse update(UUID id, CaseRequest request) {
        Case entity = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case não encontrado: " + id));
        
        String oldImagemUrl = entity.getImagemUrl();
        caseMapper.updateEntity(entity, request);

        String newImagemUrl = entity.getImagemUrl();
        if (oldImagemUrl != null && !oldImagemUrl.equals(newImagemUrl)) {
            supabaseStorageService.deleteFile(oldImagemUrl);
        }

        return caseMapper.toResponse(caseRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        Case entity = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case não encontrado: " + id));
        entity.setAtivo(false);
        caseRepository.save(entity);
    }
}
