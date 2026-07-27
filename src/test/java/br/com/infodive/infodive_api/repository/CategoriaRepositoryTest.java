package br.com.infodive.infodive_api.repository;

import static org.junit.jupiter.api.Assertions.*;

import br.com.infodive.infodive_api.entity.Categoria;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoria1;

    @BeforeEach
    void setUp() {
        categoriaRepository.deleteAll();

        categoria1 = Categoria.builder()
                .slug("ciberseguranca")
                .nome("Cibersegurança")
                .ativo(true)
                .ordem(1)
                .build();

        categoriaRepository.saveAndFlush(categoria1);
    }

    @Test
    void findBySlugAndAtivoTrue_WhenExists_ShouldReturnCategoria() {
        Optional<Categoria> found = categoriaRepository.findBySlugAndAtivoTrue("ciberseguranca");

        assertTrue(found.isPresent());
        assertEquals("Cibersegurança", found.get().getNome());
    }

    @Test
    void findAllByAtivoTrueOrderByOrdemAscNomeAsc_ShouldReturnOrderedList() {
        List<Categoria> list = categoriaRepository.findAllByAtivoTrueOrderByOrdemAscNomeAsc();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("ciberseguranca", list.get(0).getSlug());
    }
}
