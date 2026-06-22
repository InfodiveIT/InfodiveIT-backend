package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.LeadRequest;
import br.com.infodive.infodive_api.dto.response.LeadCreatedResponse;
import br.com.infodive.infodive_api.dto.response.LeadResponse;
import br.com.infodive.infodive_api.entity.Lead;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.LeadMapper;
import br.com.infodive.infodive_api.repository.LeadRepository;
import br.com.infodive.infodive_api.repository.ProdutoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    private final ProdutoRepository produtoRepository;
    private final LeadMapper leadMapper;

    @Transactional
    public LeadCreatedResponse create(LeadRequest request) {
        Lead lead = Lead.builder()
                .nomeCompleto(request.nomeCompleto())
                .email(request.email())
                .telefone(request.telefone())
                .empresa(request.empresa())
                .cargo(request.cargo())
                .mensagem(request.mensagem())
                .consentimentoLgpd(request.consentimentoLgpd())
                .build();
        if (request.produtoInteresseId() != null) {
            lead.setProdutoInteresse(produtoRepository.findById(request.produtoInteresseId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Produto não encontrado: " + request.produtoInteresseId())));
        }
        Lead salvo = leadRepository.save(lead);
        return new LeadCreatedResponse(salvo.getId(), "Lead recebido com sucesso");
    }

    @Transactional(readOnly = true)
    public List<LeadResponse> findAll() {
        return leadRepository.findAllByOrderByCriadoEmDesc()
                .stream()
                .map(leadMapper::toResponse)
                .toList();
    }
}
