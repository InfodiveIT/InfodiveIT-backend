package br.com.infodive.infodive_api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.infodive.infodive_api.dto.response.FaqResponse;
import br.com.infodive.infodive_api.service.FaqService;
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
class FaqControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FaqService faqService;

    @Test
    void findAllFaqs_ShouldReturn200OK() throws Exception {
        UUID id = UUID.randomUUID();
        FaqResponse response = new FaqResponse(id, "Pergunta?", "Resposta.", 1);

        when(faqService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/faq"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pergunta").value("Pergunta?"))
                .andExpect(jsonPath("$[0].resposta").value("Resposta."));
    }
}
