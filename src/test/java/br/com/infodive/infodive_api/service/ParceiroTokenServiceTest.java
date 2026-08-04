package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.ParceiroTokenRequest;
import br.com.infodive.infodive_api.dto.response.ParceiroTokenResponse;
import br.com.infodive.infodive_api.entity.ParceiroToken;
import br.com.infodive.infodive_api.repository.ParceiroTokenRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParceiroTokenServiceTest {

    @Mock
    private ParceiroTokenRepository repository;

    @InjectMocks
    private ParceiroTokenService service;

    private ParceiroToken tokenAtivo;

    @BeforeEach
    void setUp() {
        tokenAtivo = ParceiroToken.builder()
                .id(UUID.randomUUID())
                .nomeAgencia("Agencia Teste")
                .email("marketing@agencia.com")
                .token("infodive_pat_1234567890abcdef")
                .role("ROLE_BLOGGER")
                .expiraEm(LocalDateTime.now().plusDays(30))
                .ativo(true)
                .criadoPor("admin@infodive.com")
                .build();
    }

    @Test
    void create_DeveGerarTokenComSucesso() {
        ParceiroTokenRequest request = new ParceiroTokenRequest(
                "Agencia Teste", "marketing@agencia.com", "BLOGGER", 30
        );

        when(repository.save(any(ParceiroToken.class))).thenReturn(tokenAtivo);

        ParceiroTokenResponse response = service.create(request, "admin@infodive.com");

        assertNotNull(response);
        assertEquals("Agencia Teste", response.nomeAgencia());
        assertEquals("marketing@agencia.com", response.email());
        assertFalse(response.expirado());
        verify(repository, times(1)).save(any(ParceiroToken.class));
    }

    @Test
    void validarToken_DeveRetornarTokenSeValidoENaoExpirado() {
        when(repository.findByTokenAndAtivoTrue("infodive_pat_1234567890abcdef"))
                .thenReturn(Optional.of(tokenAtivo));

        Optional<ParceiroToken> opt = service.validarToken("infodive_pat_1234567890abcdef");

        assertTrue(opt.isPresent());
        assertEquals("Agencia Teste", opt.get().getNomeAgencia());
    }

    @Test
    void validarToken_DeveRetornarVazioSeExpirado() {
        tokenAtivo.setExpiraEm(LocalDateTime.now().minusDays(1));

        when(repository.findByTokenAndAtivoTrue("infodive_pat_1234567890abcdef"))
                .thenReturn(Optional.of(tokenAtivo));

        Optional<ParceiroToken> opt = service.validarToken("infodive_pat_1234567890abcdef");

        assertTrue(opt.isEmpty());
    }

    @Test
    void revogar_DeveDesativarToken() {
        when(repository.findById(tokenAtivo.getId())).thenReturn(Optional.of(tokenAtivo));
        when(repository.save(any(ParceiroToken.class))).thenAnswer(inv -> inv.getArgument(0));

        ParceiroTokenResponse response = service.revogar(tokenAtivo.getId());

        assertFalse(response.ativo());
        verify(repository, times(1)).save(tokenAtivo);
    }
}
