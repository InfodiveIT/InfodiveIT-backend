package br.com.infodive.infodive_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.com.infodive.infodive_api.dto.response.LogAuditoriaResponse;
import br.com.infodive.infodive_api.entity.LogAuditoria;
import br.com.infodive.infodive_api.mapper.LogAuditoriaMapper;
import br.com.infodive.infodive_api.repository.LogAuditoriaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogAuditoriaServiceTest {

    @Mock
    private LogAuditoriaRepository repository;
    @Mock
    private LogAuditoriaMapper mapper;

    @InjectMocks
    private LogAuditoriaService service;

    private LogAuditoria logAuditoria;
    private LogAuditoriaResponse responseDto;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        logAuditoria = LogAuditoria.builder()
                .id(id)
                .usuarioEmail("admin@infodive.com.br")
                .usuarioNome("admin")
                .acao("CRIAR")
                .recurso("PRODUTO")
                .recursoId(UUID.randomUUID().toString())
                .detalhes("Produto criado")
                .criadoEm(LocalDateTime.now())
                .build();

        responseDto = new LogAuditoriaResponse(id, "admin@infodive.com.br", "admin", "CRIAR", "PRODUTO", "123", "Produto criado", LocalDateTime.now());
    }

    @Test
    void findAll_ShouldReturnLogList() {
        when(repository.findAllByOrderByCriadoEmDesc()).thenReturn(List.of(logAuditoria));
        when(mapper.toResponse(logAuditoria)).thenReturn(responseDto);

        List<LogAuditoriaResponse> list = service.findAll();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("CRIAR", list.get(0).acao());
    }

    @Test
    void registrar_ShouldSaveLogAuditoriaEntry() {
        service.registrar("CRIAR", "SOLUCAO", "123", "Solução criada");

        verify(repository, times(1)).save(any(LogAuditoria.class));
    }
}
