package br.com.infodive.infodive_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.infodive.infodive_api.dto.response.CategoriaResponse;
import br.com.infodive.infodive_api.entity.Categoria;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.CategoriaMapper;
import br.com.infodive.infodive_api.repository.CategoriaRepository;
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
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;
    @Mock
    private CategoriaMapper mapper;
    @Mock
    private SupabaseStorageService supabaseStorageService;

    @InjectMocks
    private CategoriaService service;

    private Categoria categoria;
    private CategoriaResponse responseDto;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        categoria = Categoria.builder()
                .id(id)
                .slug("ciberseguranca")
                .nome("Cibersegurança")
                .ativo(true)
                .build();

        responseDto = new CategoriaResponse(id, "Cibersegurança", "ciberseguranca", 1, true, null, null);
    }

    @Test
    void findAll_ShouldReturnActiveCategorias() {
        when(repository.findAllByAtivoTrueOrderByOrdemAscNomeAsc()).thenReturn(List.of(categoria));
        when(mapper.toResponse(categoria)).thenReturn(responseDto);

        List<CategoriaResponse> list = service.findAll();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("ciberseguranca", list.get(0).slug());
    }

    @Test
    void findBySlug_WhenExists_ShouldReturnCategoria() {
        when(repository.findBySlugAndAtivoTrue("ciberseguranca")).thenReturn(Optional.of(categoria));
        when(mapper.toResponse(categoria)).thenReturn(responseDto);

        CategoriaResponse result = service.findBySlug("ciberseguranca");

        assertNotNull(result);
        assertEquals("ciberseguranca", result.slug());
    }

    @Test
    void findBySlug_WhenNotFound_ShouldThrowException() {
        when(repository.findBySlugAndAtivoTrue("invalido")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findBySlug("invalido"));
    }
}
