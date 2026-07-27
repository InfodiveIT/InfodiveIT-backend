package br.com.infodive.infodive_api.repository;

import static org.junit.jupiter.api.Assertions.*;

import br.com.infodive.infodive_api.entity.Fabricante;
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
class FabricanteRepositoryTest {

    @Autowired
    private FabricanteRepository fabricanteRepository;

    private Fabricante fabricante1;

    @BeforeEach
    void setUp() {
        fabricanteRepository.deleteAll();

        fabricante1 = Fabricante.builder()
                .slug("cisco")
                .nome("Cisco")
                .ativo(true)
                .ordem(1)
                .build();

        fabricanteRepository.saveAndFlush(fabricante1);
    }

    @Test
    void findBySlugAndAtivoTrue_WhenExists_ShouldReturnFabricante() {
        Optional<Fabricante> found = fabricanteRepository.findBySlugAndAtivoTrue("cisco");

        assertTrue(found.isPresent());
        assertEquals("Cisco", found.get().getNome());
    }

    @Test
    void findAllWithFilters_ShouldReturnFilteredList() {
        List<Fabricante> list = fabricanteRepository.findAllWithFilters(null);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("cisco", list.get(0).getSlug());
    }
}
