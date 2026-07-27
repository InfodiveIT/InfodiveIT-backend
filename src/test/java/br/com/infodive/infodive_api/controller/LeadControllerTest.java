package br.com.infodive.infodive_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.infodive.infodive_api.dto.request.LeadRequest;
import br.com.infodive.infodive_api.dto.response.LeadCreatedResponse;
import br.com.infodive.infodive_api.dto.response.LeadResponse;
import br.com.infodive.infodive_api.service.LeadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LeadService leadService;

    @Test
    void createLead_WithValidPayload_ShouldReturn201Created() throws Exception {
        UUID leadId = UUID.randomUUID();
        LeadRequest request = new LeadRequest(
                "Maria Souza", "maria@empresa.com.br", "(11) 98888-7777",
                "Empresa Y", "Diretora de TI", "Interesse em soluções", true, null
        );

        when(leadService.create(any(LeadRequest.class)))
                .thenReturn(new LeadCreatedResponse(leadId, "Lead recebido com sucesso"));

        mockMvc.perform(post("/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(leadId.toString()))
                .andExpect(jsonPath("$.message").value("Lead recebido com sucesso"));
    }

    @Test
    void createLead_WithInvalidEmail_ShouldReturn422UnprocessableEntity() throws Exception {
        LeadRequest request = new LeadRequest(
                "Maria Souza", "email-invalido", "(11) 98888-7777",
                "Empresa Y", "Diretora", "Mensagem", true, null
        );

        mockMvc.perform(post("/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAllLeads_ShouldReturnListWhenAuthenticated() throws Exception {
        UUID leadId = UUID.randomUUID();
        LeadResponse response = new LeadResponse(
                leadId, "Maria Souza", "maria@empresa.com.br", "(11) 98888-7777",
                "Empresa Y", "Diretora", "Mensagem", true, null, LocalDateTime.now()
        );

        when(leadService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(leadId.toString()))
                .andExpect(jsonPath("$[0].nomeCompleto").value("Maria Souza"));
    }
}
