package br.com.infodive.infodive_api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.infodive.infodive_api.dto.response.SolucaoResponse;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.service.SolucaoService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SolucaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SolucaoService solucaoService;

    @Test
    void findAllSolucoes_ShouldReturnList() throws Exception {
        UUID id = UUID.randomUUID();
        SolucaoResponse solucao = new SolucaoResponse(
                id, "Infraestrutura em Nuvem", "Infraestrutura em Nuvem", "infraestrutura-nuvem",
                null, null, "Descrição curta", null, null, null, List.of(), null, null, List.of(),
                null, null, null, List.of(), List.of(), 1, true, null, null, null, null
        );

        when(solucaoService.findAll()).thenReturn(List.of(solucao));

        mockMvc.perform(get("/solucoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("infraestrutura-nuvem"))
                .andExpect(jsonPath("$[0].titulo").value("Infraestrutura em Nuvem"));
    }

    @Test
    void findBySlug_WhenExists_ShouldReturn200OK() throws Exception {
        UUID id = UUID.randomUUID();
        SolucaoResponse solucao = new SolucaoResponse(
                id, "Infraestrutura em Nuvem", "Infraestrutura em Nuvem", "infraestrutura-nuvem",
                null, null, "Descrição curta", null, null, null, List.of(), null, null, List.of(),
                null, null, null, List.of(), List.of(), 1, true, null, null, null, null
        );

        when(solucaoService.findBySlug("infraestrutura-nuvem")).thenReturn(solucao);

        mockMvc.perform(get("/solucoes/infraestrutura-nuvem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("infraestrutura-nuvem"));
    }

    @Test
    void findBySlug_WhenNotFound_ShouldReturn404() throws Exception {
        when(solucaoService.findBySlug("desconhecido"))
                .thenThrow(new ResourceNotFoundException("Solução não encontrada: desconhecido"));

        mockMvc.perform(get("/solucoes/desconhecido"))
                .andExpect(status().isNotFound());
    }
}
