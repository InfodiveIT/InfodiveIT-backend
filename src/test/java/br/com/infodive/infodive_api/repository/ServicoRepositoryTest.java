package br.com.infodive.infodive_api.repository;

import static org.junit.jupiter.api.Assertions.*;

import br.com.infodive.infodive_api.entity.Servico;
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
class ServicoRepositoryTest {

    @Autowired
    private ServicoRepository servicoRepository;

    private Servico servico1;

    @BeforeEach
    void setUp() {
        servicoRepository.deleteAll();

        servico1 = Servico.builder()
                .slug("gestao-de-ti")
                .nome("Gestão de TI")
                .ativo(true)
                .ordem(1)
                .build();

        servicoRepository.saveAndFlush(servico1);
    }

    @Test
    void findBySlugAndAtivoTrue_WhenExists_ShouldReturnServico() {
        Optional<Servico> found = servicoRepository.findBySlugAndAtivoTrue("gestao-de-ti");

        assertTrue(found.isPresent());
        assertEquals("Gestão de TI", found.get().getNome());
    }

    @Test
    void findAllByAtivoTrueOrderByOrdemAscNomeAsc_ShouldReturnOrderedList() {
        List<Servico> list = servicoRepository.findAllByAtivoTrueOrderByOrdemAscNomeAsc();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("gestao-de-ti", list.get(0).getSlug());
    }
}
