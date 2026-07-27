package br.com.infodive.infodive_api.repository;

import static org.junit.jupiter.api.Assertions.*;

import br.com.infodive.infodive_api.entity.Categoria;
import br.com.infodive.infodive_api.entity.Fabricante;
import br.com.infodive.infodive_api.entity.Produto;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Produto produto1;
    private Produto produto2;

    @BeforeEach
    void setUp() {
        produtoRepository.deleteAll();

        Categoria categoria = Categoria.builder()
                .slug("seguranca")
                .nome("Segurança")
                .ativo(true)
                .build();
        entityManager.persistAndFlush(categoria);

        Fabricante fabricante = Fabricante.builder()
                .slug("cisco")
                .nome("Cisco")
                .ativo(true)
                .build();
        entityManager.persistAndFlush(fabricante);

        produto1 = Produto.builder()
                .slug("firewall-pro")
                .nome("Firewall Pro")
                .descricaoCurta("Proteção de borda")
                .destaque(true)
                .novidade(true)
                .ativo(true)
                .categoria(categoria)
                .fabricante(fabricante)
                .build();

        produto2 = Produto.builder()
                .slug("antivirus-corp")
                .nome("Antivirus Corp")
                .descricaoCurta("Proteção de endpoint")
                .destaque(false)
                .novidade(false)
                .ativo(true)
                .categoria(categoria)
                .fabricante(fabricante)
                .build();

        produtoRepository.saveAndFlush(produto1);
        produtoRepository.saveAndFlush(produto2);
    }

    @Test
    void findBySlugAndAtivoTrue_WhenExists_ShouldReturnProduto() {
        Optional<Produto> found = produtoRepository.findBySlugAndAtivoTrue("firewall-pro");

        assertTrue(found.isPresent());
        assertEquals("Firewall Pro", found.get().getNome());
    }

    @Test
    void findFirstByNovidadeTrueAndAtivoTrue_ShouldReturnNovidadeProduto() {
        Optional<Produto> novidade = produtoRepository.findFirstByNovidadeTrueAndAtivoTrue();

        assertTrue(novidade.isPresent());
        assertEquals("firewall-pro", novidade.get().getSlug());
    }

    @Test
    void countByDestaqueTrue_ShouldReturnCorrectCount() {
        long count = produtoRepository.countByDestaqueTrue();

        assertEquals(1, count);
    }

    @Test
    void findAllWithFilters_ShouldReturnFilteredPage() {
        Page<Produto> result = produtoRepository.findAllWithFilters(
                "seguranca", "cisco", true, null, PageRequest.of(0, 10)
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("firewall-pro", result.getContent().get(0).getSlug());
    }
}
