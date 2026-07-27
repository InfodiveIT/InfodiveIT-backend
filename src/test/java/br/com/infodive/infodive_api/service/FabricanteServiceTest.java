package br.com.infodive.infodive_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.infodive.infodive_api.dto.response.FabricanteResponse;
import br.com.infodive.infodive_api.entity.Fabricante;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.FabricanteMapper;
import br.com.infodive.infodive_api.repository.FabricanteRepository;
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
class FabricanteServiceTest {

    @Mock
    private FabricanteRepository fabricanteRepository;
    @Mock
    private FabricanteMapper fabricanteMapper;
    @Mock
    private SupabaseStorageService supabaseStorageService;

    @InjectMocks
    private FabricanteService fabricanteService;

    private Fabricante fabricante;
    private FabricanteResponse responseDto;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        fabricante = Fabricante.builder()
                .id(id)
                .slug("cisco")
                .nome("Cisco")
                .ativo(true)
                .build();

        responseDto = new FabricanteResponse(
                id, "Cisco", "cisco", "Descrição", "Descrição curta",
                "https://logo.png", "https://cisco.com", true, 1, true, List.of(), null, null
        );
    }

    @Test
    void findAll_ShouldReturnActiveFabricantes() {
        when(fabricanteRepository.findAllWithFilters(null)).thenReturn(List.of(fabricante));
        when(fabricanteMapper.toResponse(fabricante)).thenReturn(responseDto);

        List<FabricanteResponse> result = fabricanteService.findAll(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("cisco", result.get(0).slug());
    }

    @Test
    void findBySlug_WhenExists_ShouldReturnFabricanteResponse() {
        when(fabricanteRepository.findBySlugAndAtivoTrue("cisco")).thenReturn(Optional.of(fabricante));
        when(fabricanteMapper.toResponse(fabricante)).thenReturn(responseDto);

        FabricanteResponse result = fabricanteService.findBySlug("cisco");

        assertNotNull(result);
        assertEquals("Cisco", result.nome());
    }

    @Test
    void findBySlug_WhenNotFound_ShouldThrowException() {
        when(fabricanteRepository.findBySlugAndAtivoTrue("invalido")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fabricanteService.findBySlug("invalido"));
    }
}
