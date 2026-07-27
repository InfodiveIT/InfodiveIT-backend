package br.com.infodive.infodive_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.com.infodive.infodive_api.dto.request.FaqRequest;
import br.com.infodive.infodive_api.dto.response.FaqResponse;
import br.com.infodive.infodive_api.entity.Faq;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.FaqRepository;
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
class FaqServiceTest {

    @Mock
    private FaqRepository faqRepository;

    @InjectMocks
    private FaqService faqService;

    private Faq faq;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        faq = Faq.builder()
                .id(id)
                .pergunta("Como funciona a garantia?")
                .resposta("Suporte 24/7 com SLA garantido.")
                .ordem(1)
                .ativo(true)
                .build();
    }

    @Test
    void findAll_ShouldReturnActiveFaqs() {
        when(faqRepository.findAllByAtivoTrueOrderByOrdemAsc()).thenReturn(List.of(faq));

        List<FaqResponse> list = faqService.findAll();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("Como funciona a garantia?", list.get(0).pergunta());
    }

    @Test
    void create_ShouldSaveAndReturnResponse() {
        FaqRequest request = new FaqRequest("Como funciona a garantia?", "Suporte 24/7 com SLA garantido.", 1);
        when(faqRepository.save(any(Faq.class))).thenReturn(faq);

        FaqResponse response = faqService.create(request);

        assertNotNull(response);
        assertEquals("Como funciona a garantia?", response.pergunta());
    }

    @Test
    void findById_WhenNotFound_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(faqRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> faqService.findById(id));
    }
}
