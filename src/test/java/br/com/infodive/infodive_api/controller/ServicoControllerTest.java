package br.com.infodive.infodive_api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.infodive.infodive_api.dto.response.ServicoResponse;
import br.com.infodive.infodive_api.service.ServicoService;
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
class ServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicoService servicoService;

    @Test
    void findAllServicos_ShouldReturn200OK() throws Exception {
        UUID id = UUID.randomUUID();
        ServicoResponse response = new ServicoResponse(id, "Consultoria", "consultoria", "Descrição", "icon", 1, true, null, null);

        when(servicoService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("consultoria"))
                .andExpect(jsonPath("$[0].nome").value("Consultoria"));
    }
}
