package br.com.infodive.infodive_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.infodive.infodive_api.dto.response.ServicoResponse;
import br.com.infodive.infodive_api.entity.Servico;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.ServicoMapper;
import br.com.infodive.infodive_api.repository.ServicoRepository;
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
class ServicoServiceTest {

    @Mock
    private ServicoRepository servicoRepository;
    @Mock
    private ServicoMapper servicoMapper;

    @InjectMocks
    private ServicoService servicoService;

    private Servico servico;
    private ServicoResponse responseDto;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        servico = Servico.builder()
                .id(id)
                .slug("consultoria-ti")
                .nome("Consultoria de TI")
                .ativo(true)
                .build();

        responseDto = new ServicoResponse(
                id, "Consultoria de TI", "consultoria-ti", "Descrição", "icon", 1, true, null, null
        );
    }

    @Test
    void findAll_ShouldReturnActiveServicos() {
        when(servicoRepository.findAllByAtivoTrueOrderByOrdemAscNomeAsc()).thenReturn(List.of(servico));
        when(servicoMapper.toResponse(servico)).thenReturn(responseDto);

        List<ServicoResponse> list = servicoService.findAll();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("consultoria-ti", list.get(0).slug());
    }

    @Test
    void findBySlug_WhenExists_ShouldReturnServico() {
        when(servicoRepository.findBySlugAndAtivoTrue("consultoria-ti")).thenReturn(Optional.of(servico));
        when(servicoMapper.toResponse(servico)).thenReturn(responseDto);

        ServicoResponse result = servicoService.findBySlug("consultoria-ti");

        assertNotNull(result);
        assertEquals("consultoria-ti", result.slug());
    }

    @Test
    void findBySlug_WhenNotFound_ShouldThrowException() {
        when(servicoRepository.findBySlugAndAtivoTrue("invalido")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> servicoService.findBySlug("invalido"));
    }
}
