package br.com.infodive.infodive_api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.infodive.infodive_api.entity.AdminAutorizado;
import br.com.infodive.infodive_api.repository.AdminAutorizadoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(properties = "jwt.mock-entra-id=true")
@AutoConfigureMockMvc
class SecurityFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminAutorizadoRepository adminRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TEST_EMAIL = "test-admin@infodive.com.br";

    @BeforeEach
    void setUp() {
        adminRepository.deleteAll();
        
        // Cadastra um administrador de teste ativo na allowlist
        AdminAutorizado admin = AdminAutorizado.builder()
                .email(TEST_EMAIL)
                .nome("Test Admin")
                .ativo(true)
                .build();
        adminRepository.save(admin);
    }

    @Test
    void publicGetEndpointsShouldBeAccessibleWithoutAuthentication() throws Exception {
        // GET público em fabricantes deve retornar 200 OK (mesmo se vazio)
        mockMvc.perform(get("/fabricantes"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedWriteEndpointsShouldBeBlockedWithoutAuthentication() throws Exception {
        // POST protegido em fabricantes deve retornar 403 Forbidden
        mockMvc.perform(post("/fabricantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void loginWithAuthorizedEmailShouldGenerateJwtToken() throws Exception {
        // POST login com email autorizado na allowlist
        Map<String, String> request = Map.of("idToken", "mock:" + TEST_EMAIL);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
    }

    @Test
    void loginWithUnauthorizedEmailShouldFail() throws Exception {
        // POST login com email que NÃO está na allowlist
        Map<String, String> request = Map.of("idToken", "mock:unauthorized@domain.com");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessingProtectedEndpointsWithValidJwtTokenShouldBeAllowed() throws Exception {
        // 1. Fazer login para obter o JWT
        Map<String, String> loginReq = Map.of("idToken", "mock:" + TEST_EMAIL);
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Map<?, ?> responseMap = objectMapper.readValue(responseBody, Map.class);
        String jwtToken = (String) responseMap.get("token");

        // 2. Acessar o endpoint protegido usando o token JWT local
        // Deve retornar 422 Unprocessable Entity (erro de validação do JSON vazio)
        // em vez de 403 Forbidden (bloqueio de segurança)
        mockMvc.perform(post("/fabricantes")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableEntity());
    }
}
