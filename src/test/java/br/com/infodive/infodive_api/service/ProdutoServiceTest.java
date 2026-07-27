package br.com.infodive.infodive_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.com.infodive.infodive_api.dto.request.ProdutoRequest;
import br.com.infodive.infodive_api.dto.response.ProdutoDetalheResponse;
import br.com.infodive.infodive_api.entity.Produto;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.ProdutoMapper;
import br.com.infodive.infodive_api.repository.CategoriaRepository;
import br.com.infodive.infodive_api.repository.FabricanteRepository;
import br.com.infodive.infodive_api.repository.ProdutoRepository;
import br.com.infodive.infodive_api.repository.ServicoRepository;
import br.com.infodive.infodive_api.repository.SolucaoRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private FabricanteRepository fabricanteRepository;
    @Mock
    private SolucaoRepository solucaoRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private ServicoRepository servicoRepository;
    @Mock
    private ProdutoMapper produtoMapper;
    @Mock
    private SupabaseStorageService supabaseStorageService;
    @Mock
    private LogAuditoriaService logAuditoriaService;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private ProdutoDetalheResponse produtoDetalheResponse;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        produto = Produto.builder()
                .id(id)
                .slug("firewall-nextgen")
                .nome("Firewall Next-Gen")
                .descricaoCurta("Proteção de rede avançada")
                .destaque(false)
                .novidade(false)
                .ativo(true)
                .build();

        produtoDetalheResponse = new ProdutoDetalheResponse(
                id, "Firewall Next-Gen", "firewall-nextgen", "Subcategoria",
                "Proteção de rede avançada", "Descrição completa", List.of(), List.of(),
                "Eyebrow", "Titulo", "Descricao", "https://img.png", "https://oficial.com",
                false, false, true, null, null, null, null, null, null, null, null, null,
                List.of(), List.of(), null, null
        );
    }

    @Test
    void findBySlug_WhenExists_ShouldReturnProdutoDetalhe() {
        when(produtoRepository.findBySlugAndAtivoTrue("firewall-nextgen")).thenReturn(Optional.of(produto));
        when(produtoMapper.toDetalheResponse(produto)).thenReturn(produtoDetalheResponse);

        ProdutoDetalheResponse result = produtoService.findBySlug("firewall-nextgen");

        assertNotNull(result);
        assertEquals("firewall-nextgen", result.slug());
        assertEquals("Firewall Next-Gen", result.nome());
    }

    @Test
    void findBySlug_WhenNotFound_ShouldThrowResourceNotFoundException() {
        when(produtoRepository.findBySlugAndAtivoTrue("invalido")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> produtoService.findBySlug("invalido"));
    }

    @Test
    void create_WhenDestaqueLimitExceeded_ShouldThrowIllegalArgumentException() {
        ProdutoRequest request = new ProdutoRequest(
                "novo-produto", "Novo Produto", null, "Descrição",
                null, null, null, null, null, null, null, null,
                true, false, null, null, null, null
        );

        when(produtoRepository.countByDestaqueTrue()).thenReturn(6L);

        assertThrows(IllegalArgumentException.class, () -> produtoService.create(request));
    }

    @Test
    void create_WhenValid_ShouldSaveAndReturnResponse() {
        ProdutoRequest request = new ProdutoRequest(
                "novo-produto", "Novo Produto", null, "Descrição",
                null, null, null, null, null, null, null, null,
                false, false, null, null, null, null
        );

        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(produtoMapper.toDetalheResponse(produto)).thenReturn(produtoDetalheResponse);

        ProdutoDetalheResponse result = produtoService.create(request);

        assertNotNull(result);
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }
}
