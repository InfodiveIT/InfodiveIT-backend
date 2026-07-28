package br.com.infodive.infodive_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import br.com.infodive.infodive_api.dto.request.PoliticaRequest;
import br.com.infodive.infodive_api.dto.response.PoliticaResponse;
import br.com.infodive.infodive_api.entity.Politica;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.PoliticaMapper;
import br.com.infodive.infodive_api.repository.PoliticaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PoliticaServiceTest {

    @Mock
    private PoliticaRepository repository;
    @Mock
    private PoliticaMapper mapper;

    @InjectMocks
    private PoliticaService service;

    private Politica politica;
    private PoliticaResponse responseDto;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        politica = Politica.builder()
                .id(id)
                .slug("politica-de-cookies")
                .titulo("Política de Cookies")
                .subtitulo("Cookies")
                .conteudo("Conteúdo de cookies")
                .ultimaAtualizacao("28 de Julho de 2026")
                .ativo(true)
                .build();

        responseDto = new PoliticaResponse(
                id, "politica-de-cookies", "Política de Cookies", "Cookies",
                "Conteúdo de cookies", "28 de Julho de 2026", true, null, null
        );

        when(repository.findBySlug(anyString())).thenReturn(Optional.of(politica));
    }

    @Test
    void findAll_ShouldReturnPoliticasList() {
        when(repository.findAllByOrderByTituloAsc()).thenReturn(List.of(politica));
        when(mapper.toResponse(politica)).thenReturn(responseDto);

        List<PoliticaResponse> list = service.findAll();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("politica-de-cookies", list.get(0).slug());
    }

    @Test
    void findBySlug_WhenExists_ShouldReturnPolitica() {
        when(repository.findBySlugAndAtivoTrue("politica-de-cookies")).thenReturn(Optional.of(politica));
        when(mapper.toResponse(politica)).thenReturn(responseDto);

        PoliticaResponse result = service.findBySlug("politica-de-cookies");

        assertNotNull(result);
        assertEquals("Política de Cookies", result.titulo());
    }

    @Test
    void findBySlug_WhenNotFound_ShouldThrowException() {
        when(repository.findBySlugAndAtivoTrue("invalida")).thenReturn(Optional.empty());
        when(repository.findBySlug("invalida")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findBySlug("invalida"));
    }

    @Test
    void create_ShouldSaveAndReturnPolitica() {
        PoliticaRequest request = new PoliticaRequest("politica-nova", "Política Nova", "Subtitulo", "Conteudo", "28/07", true);
        when(repository.findBySlug("politica-nova")).thenReturn(Optional.empty());
        when(mapper.toEntity(request)).thenReturn(politica);
        when(repository.save(any(Politica.class))).thenReturn(politica);
        when(mapper.toResponse(politica)).thenReturn(responseDto);

        PoliticaResponse result = service.create(request);

        assertNotNull(result);
        assertEquals("politica-de-cookies", result.slug());
    }
}
