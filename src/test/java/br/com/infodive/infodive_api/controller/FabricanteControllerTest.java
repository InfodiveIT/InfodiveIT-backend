package br.com.infodive.infodive_api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.infodive.infodive_api.dto.response.FabricanteResponse;
import br.com.infodive.infodive_api.service.FabricanteService;
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
class FabricanteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FabricanteService fabricanteService;

    @Test
    void findAllFabricantes_ShouldReturn200OK() throws Exception {
        UUID id = UUID.randomUUID();
        FabricanteResponse response = new FabricanteResponse(
                id, "Cisco", "cisco", "Descrição", "Descrição curta",
                "https://logo.png", "https://cisco.com", true, 1, true, List.of(), null, null
        );

        when(fabricanteService.findAll(null)).thenReturn(List.of(response));

        mockMvc.perform(get("/fabricantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("cisco"))
                .andExpect(jsonPath("$[0].nome").value("Cisco"));
    }
}
