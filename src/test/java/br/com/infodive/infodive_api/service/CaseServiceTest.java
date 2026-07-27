package br.com.infodive.infodive_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.infodive.infodive_api.dto.response.CaseResponse;
import br.com.infodive.infodive_api.entity.Case;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.CaseMapper;
import br.com.infodive.infodive_api.repository.CaseRepository;
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
class CaseServiceTest {

    @Mock
    private CaseRepository caseRepository;
    @Mock
    private CaseMapper caseMapper;
    @Mock
    private SupabaseStorageService supabaseStorageService;

    @InjectMocks
    private CaseService caseService;

    private Case aCase;
    private CaseResponse responseDto;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        aCase = Case.builder()
                .id(id)
                .titulo("Case de Sucesso X")
                .cliente("Empresa Z")
                .ativo(true)
                .build();

        responseDto = new CaseResponse(
                id, "Setor", "Empresa Z", "Case de Sucesso X", "Desafio",
                "Resultado", "100%", "Autor", "Cargo", "Depoimento", "https://img.png", 1, true
        );
    }

    @Test
    void findAll_ShouldReturnActiveCases() {
        when(caseRepository.findAllByAtivoTrueOrderByOrdemAsc()).thenReturn(List.of(aCase));
        when(caseMapper.toResponse(aCase)).thenReturn(responseDto);

        List<CaseResponse> result = caseService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Case de Sucesso X", result.get(0).titulo());
    }

    @Test
    void findById_WhenNotFound_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(caseRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> caseService.findById(id));
    }
}
