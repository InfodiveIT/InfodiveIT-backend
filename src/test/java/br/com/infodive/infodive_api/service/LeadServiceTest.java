package br.com.infodive.infodive_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.com.infodive.infodive_api.dto.request.LeadRequest;
import br.com.infodive.infodive_api.dto.response.LeadCreatedResponse;
import br.com.infodive.infodive_api.dto.response.LeadResponse;
import br.com.infodive.infodive_api.entity.Lead;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.LeadMapper;
import br.com.infodive.infodive_api.repository.LeadRepository;
import br.com.infodive.infodive_api.repository.ProdutoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private LeadMapper leadMapper;

    @InjectMocks
    private LeadService leadService;

    private Lead lead;
    private LeadRequest leadRequest;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        lead = Lead.builder()
                .id(id)
                .nomeCompleto("João Silva")
                .email("joao@empresa.com.br")
                .telefone("(11) 99999-8888")
                .empresa("Empresa X")
                .cargo("CTO")
                .mensagem("Gostaria de saber mais")
                .consentimentoLgpd(true)
                .criadoEm(LocalDateTime.now())
                .build();

        leadRequest = new LeadRequest(
                "João Silva", "joao@empresa.com.br", "(11) 99999-8888",
                "Empresa X", "CTO", "Gostaria de saber mais", true, null
        );
    }

    @Test
    void create_WithoutProduto_ShouldSaveLeadSuccessfully() {
        when(leadRepository.save(any(Lead.class))).thenReturn(lead);

        LeadCreatedResponse response = leadService.create(leadRequest);

        assertNotNull(response);
        assertEquals(lead.getId(), response.id());
        assertEquals("Lead recebido com sucesso", response.message());
        verify(leadRepository, times(1)).save(any(Lead.class));
    }

    @Test
    void create_WithNonExistentProduto_ShouldThrowResourceNotFoundException() {
        UUID produtoId = UUID.randomUUID();
        LeadRequest reqWithProduto = new LeadRequest(
                "João Silva", "joao@empresa.com.br", "(11) 99999-8888",
                "Empresa X", "CTO", "Gostaria de saber mais", true, produtoId
        );

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> leadService.create(reqWithProduto));
    }

    @Test
    void findAll_ShouldReturnAllLeadsOrdered() {
        LeadResponse responseDto = new LeadResponse(
                lead.getId(), "João Silva", "joao@empresa.com.br", "(11) 99999-8888",
                "Empresa X", "CTO", "Gostaria de saber mais", true, null, LocalDateTime.now()
        );

        when(leadRepository.findAllByOrderByCriadoEmDesc()).thenReturn(List.of(lead));
        when(leadMapper.toResponse(lead)).thenReturn(responseDto);

        List<LeadResponse> list = leadService.findAll();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("João Silva", list.get(0).nomeCompleto());
    }
}
