package br.com.infodive.infodive_api.repository;

import static org.junit.jupiter.api.Assertions.*;

import br.com.infodive.infodive_api.entity.Conteudo;
import br.com.infodive.infodive_api.entity.TipoConteudo;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ConteudoRepositoryTest {

    @Autowired
    private ConteudoRepository conteudoRepository;

    private Conteudo artigo1;

    @BeforeEach
    void setUp() {
        conteudoRepository.deleteAll();

        artigo1 = Conteudo.builder()
                .slug("tendencias-seguranca-2026")
                .titulo("Tendências de Segurança em TI 2026")
                .descricao("Um guia completo sobre cibersegurança")
                .tipo(TipoConteudo.ARTIGO)
                .destaque(true)
                .ativo(true)
                .publicadoEm(LocalDateTime.now())
                .build();

        conteudoRepository.save(artigo1);
    }

    @Test
    void findBySlugAndAtivoTrue_WhenExists_ShouldReturnConteudo() {
        Optional<Conteudo> found = conteudoRepository.findBySlugAndAtivoTrue("tendencias-seguranca-2026");

        assertTrue(found.isPresent());
        assertEquals("Tendências de Segurança em TI 2026", found.get().getTitulo());
    }

    @Test
    void countByAtivoTrueAndDestaqueTrue_ShouldReturnCorrectCount() {
        long count = conteudoRepository.countByAtivoTrueAndDestaqueTrue();

        assertEquals(1, count);
    }

    @Test
    void findAllWithFilters_WhenFilteredByTipo_ShouldReturnMatchingItems() {
        Page<Conteudo> page = conteudoRepository.findAllWithFilters(
                TipoConteudo.ARTIGO, null, PageRequest.of(0, 10)
        );

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        assertEquals("tendencias-seguranca-2026", page.getContent().get(0).getSlug());
    }
}
