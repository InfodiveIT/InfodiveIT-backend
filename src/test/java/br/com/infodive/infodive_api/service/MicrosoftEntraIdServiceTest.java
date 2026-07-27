package br.com.infodive.infodive_api.service;

import static org.junit.jupiter.api.Assertions.*;

import br.com.infodive.infodive_api.service.MicrosoftEntraIdService.EntraIdUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class MicrosoftEntraIdServiceTest {

    private MicrosoftEntraIdService entraIdService;

    @BeforeEach
    void setUp() {
        entraIdService = new MicrosoftEntraIdService();
        ReflectionTestUtils.setField(entraIdService, "mockEntraId", true);
    }

    @Test
    void validateAndExtract_WithNullOrEmpty_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> entraIdService.validateAndExtract(null));
        assertThrows(IllegalArgumentException.class, () -> entraIdService.validateAndExtract(""));
    }

    @Test
    void validateAndExtract_WithMockPrefix_ShouldReturnUser() {
        EntraIdUser user = entraIdService.validateAndExtract("mock:lucas@infodive.com.br");

        assertNotNull(user);
        assertEquals("lucas@infodive.com.br", user.email());
        assertEquals("lucas", user.nome());
    }

    @Test
    void validateAndExtract_WithMockEmail_ShouldReturnUser() {
        EntraIdUser user = entraIdService.validateAndExtract("admin@infodive.com.br");

        assertNotNull(user);
        assertEquals("admin@infodive.com.br", user.email());
        assertEquals("admin", user.nome());
    }
}
