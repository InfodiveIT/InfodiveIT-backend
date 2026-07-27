package br.com.infodive.infodive_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.infodive.infodive_api.dto.response.SolucaoResponse;
import br.com.infodive.infodive_api.entity.Solucao;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.SolucaoMapper;
import br.com.infodive.infodive_api.repository.CategoriaRepository;
import br.com.infodive.infodive_api.repository.FabricanteRepository;
import br.com.infodive.infodive_api.repository.SolucaoRepository;
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
class SolucaoServiceTest {

    @Mock
    private SolucaoRepository solucaoRepository;
    @Mock
    private FabricanteRepository fabricanteRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private SolucaoMapper solucaoMapper;
    @Mock
    private SupabaseStorageService supabaseStorageService;

    @InjectMocks
    private SolucaoService solucaoService;

    private Solucao solucao;
    private SolucaoResponse solucaoResponse;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        solucao = Solucao.builder()
                .id(id)
                .slug("cloud-security")
                .titulo("Segurança em Nuvem")
                .descricaoCurta("Proteção avançada para infraestrutura em nuvem")
                .ativo(true)
                .build();

        solucaoResponse = new SolucaoResponse(
                id, "Segurança em Nuvem", "Segurança em Nuvem", "cloud-security",
                null, null, "Proteção avançada para infraestrutura em nuvem",
                null, null, null, List.of(), null, null, List.of(), null,
                null, null, List.of(), List.of(), 1, true, null, null, null, null
        );
    }

    @Test
    void findAll_ShouldReturnActiveSolucoes() {
        when(solucaoRepository.findAllByAtivoTrueOrderByOrdemAscTituloAsc()).thenReturn(List.of(solucao));
        when(solucaoMapper.toResponse(solucao)).thenReturn(solucaoResponse);

        List<SolucaoResponse> result = solucaoService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("cloud-security", result.get(0).slug());
    }

    @Test
    void findBySlug_WhenExists_ShouldReturnSolucao() {
        when(solucaoRepository.findBySlugAndAtivoTrue("cloud-security")).thenReturn(Optional.of(solucao));
        when(solucaoMapper.toResponse(solucao)).thenReturn(solucaoResponse);

        SolucaoResponse result = solucaoService.findBySlug("cloud-security");

        assertNotNull(result);
        assertEquals("cloud-security", result.slug());
    }

    @Test
    void findBySlug_WhenNotFound_ShouldThrowResourceNotFoundException() {
        when(solucaoRepository.findBySlugAndAtivoTrue("desconhecido")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> solucaoService.findBySlug("desconhecido"));
    }

    @Test
    void delete_WhenExists_ShouldDeleteAndRemoveImage() {
        UUID id = solucao.getId();
        solucao.setImagemUrl("https://supabase.co/storage/v1/object/public/images/test.jpg");
        when(solucaoRepository.findById(id)).thenReturn(Optional.of(solucao));

        solucaoService.delete(id);

        verify(supabaseStorageService, times(1)).deleteFile(solucao.getImagemUrl());
        verify(solucaoRepository, times(1)).delete(solucao);
    }
}
