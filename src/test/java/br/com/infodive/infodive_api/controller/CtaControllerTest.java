package br.com.infodive.infodive_api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.infodive.infodive_api.dto.response.CtaResponse;
import br.com.infodive.infodive_api.service.CtaService;
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
class CtaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CtaService ctaService;

    @Test
    void findAll_ShouldReturn200OK() throws Exception {
        UUID id = UUID.randomUUID();
        CtaResponse response = new CtaResponse(id, "home", "Transforme sua TI", "Subtitulo", "Falar com especialista", "MODAL_CONTATO");

        when(ctaService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/ctas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pagina").value("home"))
                .andExpect(jsonPath("$[0].titulo").value("Transforme sua TI"));
    }

    @Test
    void findByIdentifier_ShouldReturn200OK() throws Exception {
        UUID id = UUID.randomUUID();
        CtaResponse response = new CtaResponse(id, "home", "Transforme sua TI", "Subtitulo", "Falar com especialista", "MODAL_CONTATO");

        when(ctaService.findByIdentifier("home")).thenReturn(response);

        mockMvc.perform(get("/ctas/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagina").value("home"));
    }
}
