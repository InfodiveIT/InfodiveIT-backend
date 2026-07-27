package br.com.infodive.infodive_api.repository;

import static org.junit.jupiter.api.Assertions.*;

import br.com.infodive.infodive_api.entity.Case;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CaseRepositoryTest {

    @Autowired
    private CaseRepository caseRepository;

    private Case case1;

    @BeforeEach
    void setUp() {
        caseRepository.deleteAll();

        case1 = Case.builder()
                .segmento("Tecnologia")
                .cliente("Cliente Alpha")
                .titulo("Case de Sucesso Alpha")
                .desafio("Desafio técnico")
                .resultado("Resultado positivo")
                .metrica("100% disponibilidade")
                .autor("Autor Teste")
                .cargo("CTO")
                .depoimento("Excelente trabalho")
                .ativo(true)
                .ordem(1)
                .build();

        caseRepository.saveAndFlush(case1);
    }

    @Test
    void findAllByAtivoTrueOrderByOrdemAsc_ShouldReturnOrderedCases() {
        List<Case> list = caseRepository.findAllByAtivoTrueOrderByOrdemAsc();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("Case de Sucesso Alpha", list.get(0).getTitulo());
    }
}
