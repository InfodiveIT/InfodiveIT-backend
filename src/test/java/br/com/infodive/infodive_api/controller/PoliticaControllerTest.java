package br.com.infodive.infodive_api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.infodive.infodive_api.dto.response.PoliticaResponse;
import br.com.infodive.infodive_api.service.PoliticaService;
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
class PoliticaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PoliticaService service;

    @Test
    void findAll_ShouldReturn200OK() throws Exception {
        UUID id = UUID.randomUUID();
        PoliticaResponse response = new PoliticaResponse(
                id, "politica-de-cookies", "Política de Cookies", "Cookies",
                "Conteudo", "28/07/2026", true, null, null
        );

        when(service.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/politicas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("politica-de-cookies"))
                .andExpect(jsonPath("$[0].titulo").value("Política de Cookies"));
    }

    @Test
    void findByIdentifier_ShouldReturn200OK() throws Exception {
        UUID id = UUID.randomUUID();
        PoliticaResponse response = new PoliticaResponse(
                id, "politica-de-cookies", "Política de Cookies", "Cookies",
                "Conteudo", "28/07/2026", true, null, null
        );

        when(service.findBySlug("politica-de-cookies")).thenReturn(response);

        mockMvc.perform(get("/politicas/politica-de-cookies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("politica-de-cookies"));
    }
}
