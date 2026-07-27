package br.com.infodive.infodive_api.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.infodive.infodive_api.dto.response.ProdutoDetalheResponse;
import br.com.infodive.infodive_api.dto.response.ProdutoResumoResponse;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.service.ProdutoService;
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
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService produtoService;

    @Test
    void findAllProdutos_ShouldReturnPagedResults() throws Exception {
        UUID id = UUID.randomUUID();
        ProdutoResumoResponse resumo = new ProdutoResumoResponse(
                id, "Firewall Next-Gen", "firewall-nextgen", "Segurança", "Descrição curta",
                "https://imagem.png", false, false, "seguranca", "Segurança",
                "solucao-slug", "Solução", "cisco", "Cisco", "https://logo.png"
        );

        when(produtoService.findAll(anyString(), anyString(), eq(null), eq(null), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(resumo)));

        mockMvc.perform(get("/produtos")
                        .param("categoria", "seguranca")
                        .param("fabricante", "cisco"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].slug").value("firewall-nextgen"))
                .andExpect(jsonPath("$.content[0].nome").value("Firewall Next-Gen"));
    }

    @Test
    void findByIdentifier_BySlug_ShouldReturn200OK() throws Exception {
        UUID id = UUID.randomUUID();
        ProdutoDetalheResponse detalhe = new ProdutoDetalheResponse(
                id, "Firewall Next-Gen", "firewall-nextgen", "Subcategoria",
                "Descrição curta", "Descrição completa", List.of(), List.of(),
                "Eyebrow", "Titulo", "Descricao", "https://img.png", "https://oficial.com",
                false, false, true, null, null, null, null, null, null, null, null, null,
                List.of(), List.of(), null, null
        );

        when(produtoService.findBySlug("firewall-nextgen")).thenReturn(detalhe);

        mockMvc.perform(get("/produtos/firewall-nextgen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("firewall-nextgen"))
                .andExpect(jsonPath("$.nome").value("Firewall Next-Gen"));
    }

    @Test
    void findByIdentifier_WhenNotFound_ShouldReturn404() throws Exception {
        when(produtoService.findBySlug("nao-existente"))
                .thenThrow(new ResourceNotFoundException("Produto não encontrado: nao-existente"));

        mockMvc.perform(get("/produtos/nao-existente"))
                .andExpect(status().isNotFound());
    }
}
