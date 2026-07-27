package br.com.infodive.infodive_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.infodive.infodive_api.dto.response.ConteudoResponse;
import br.com.infodive.infodive_api.entity.Conteudo;
import br.com.infodive.infodive_api.entity.TipoConteudo;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.ConteudoMapper;
import br.com.infodive.infodive_api.repository.ConteudoRepository;
import br.com.infodive.infodive_api.repository.FabricanteRepository;
import br.com.infodive.infodive_api.repository.ProdutoRepository;
import br.com.infodive.infodive_api.repository.SolucaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConteudoServiceTest {

    @Mock
    private ConteudoRepository conteudoRepository;
    @Mock
    private SolucaoRepository solucaoRepository;
    @Mock
    private FabricanteRepository fabricanteRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private ConteudoMapper conteudoMapper;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private SupabaseStorageService supabaseStorageService;

    @InjectMocks
    private ConteudoService conteudoService;

    private Conteudo conteudo;
    private ConteudoResponse responseDto;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        conteudo = Conteudo.builder()
                .id(id)
                .slug("artigo-seguranca")
                .titulo("Artigo sobre Segurança")
                .descricao("Descrição do artigo")
                .tipo(TipoConteudo.ARTIGO)
                .ativo(true)
                .destaque(false)
                .publicadoEm(LocalDateTime.now())
                .build();

        responseDto = new ConteudoResponse(
                id, "Artigo sobre Segurança", "artigo-seguranca", TipoConteudo.ARTIGO,
                "Descrição do artigo", "https://img.png", "Autor", "5 min", null, null,
                LocalDateTime.now(), true, false, null, null, null, null, null
        );
    }

    @Test
    void findBySlug_WhenExists_ShouldReturnConteudo() {
        when(conteudoRepository.findBySlugAndAtivoTrue("artigo-seguranca")).thenReturn(Optional.of(conteudo));
        when(conteudoMapper.toResponse(conteudo)).thenReturn(responseDto);

        ConteudoResponse result = conteudoService.findBySlug("artigo-seguranca");

        assertNotNull(result);
        assertEquals("artigo-seguranca", result.slug());
    }

    @Test
    void findBySlug_WhenNotFound_ShouldThrowResourceNotFoundException() {
        when(conteudoRepository.findBySlugAndAtivoTrue("invalido")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> conteudoService.findBySlug("invalido"));
    }

    @Test
    void delete_WhenExists_ShouldDeleteAndRemoveImage() {
        UUID id = conteudo.getId();
        conteudo.setImagemUrl("https://supabase.co/storage/v1/object/public/images/artigo.jpg");
        when(conteudoRepository.findById(id)).thenReturn(Optional.of(conteudo));

        conteudoService.delete(id);

        verify(supabaseStorageService, times(1)).deleteFile(conteudo.getImagemUrl());
        verify(conteudoRepository, times(1)).delete(conteudo);
    }
}
