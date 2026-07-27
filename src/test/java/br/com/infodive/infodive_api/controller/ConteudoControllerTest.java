package br.com.infodive.infodive_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.infodive.infodive_api.dto.response.ConteudoResponse;
import br.com.infodive.infodive_api.entity.TipoConteudo;
import br.com.infodive.infodive_api.service.ConteudoService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ConteudoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConteudoService conteudoService;

    @Test
    void findAllConteudos_ShouldReturnPagedList() throws Exception {
        UUID id = UUID.randomUUID();
        ConteudoResponse response = new ConteudoResponse(
                id, "Artigo sobre TI", "artigo-ti", TipoConteudo.ARTIGO,
                "Descrição", "https://img.png", "Autor", "5 min", null, null,
                LocalDateTime.now(), true, false, null, null, null, null, null
        );

        when(conteudoService.findAll(any(), eq(null), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/conteudos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].slug").value("artigo-ti"))
                .andExpect(jsonPath("$.content[0].titulo").value("Artigo sobre TI"));
    }

    @Test
    void findBySlug_ShouldReturn200OK() throws Exception {
        UUID id = UUID.randomUUID();
        ConteudoResponse response = new ConteudoResponse(
                id, "Artigo sobre TI", "artigo-ti", TipoConteudo.ARTIGO,
                "Descrição", "https://img.png", "Autor", "5 min", null, null,
                LocalDateTime.now(), true, false, null, null, null, null, null
        );

        when(conteudoService.findBySlug("artigo-ti")).thenReturn(response);

        mockMvc.perform(get("/conteudos/artigo-ti"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("artigo-ti"));
    }
}
