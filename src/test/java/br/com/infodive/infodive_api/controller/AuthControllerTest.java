package br.com.infodive.infodive_api.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.infodive.infodive_api.dto.request.LoginRequest;
import br.com.infodive.infodive_api.service.JwtService;
import br.com.infodive.infodive_api.service.MicrosoftEntraIdService;
import br.com.infodive.infodive_api.service.MicrosoftEntraIdService.EntraIdUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MicrosoftEntraIdService entraIdService;

    @MockBean
    private JwtService jwtService;

    @Test
    void login_WithValidInfodiveEmail_ShouldReturnJwtToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("mock:admin@infodive.com.br", null, null);
        EntraIdUser mockUser = new EntraIdUser("admin@infodive.com.br", "Admin Infodive");

        when(entraIdService.validateAndExtract(anyString())).thenReturn(mockUser);
        when(jwtService.generateToken("admin@infodive.com.br", "Admin Infodive", "ROLE_ADMIN"))
                .thenReturn("mocked.jwt.token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.email").value("admin@infodive.com.br"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    void login_WithoutIdTokenOrPartnerKey_ShouldReturn400() throws Exception {
        LoginRequest loginRequest = new LoginRequest(null, null, null);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
}
