package br.com.infodive.infodive_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.com.infodive.infodive_api.dto.request.AdminAutorizadoRequest;
import br.com.infodive.infodive_api.dto.response.AdminAutorizadoResponse;
import br.com.infodive.infodive_api.entity.AdminAutorizado;
import br.com.infodive.infodive_api.exception.BusinessException;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.AdminAutorizadoMapper;
import br.com.infodive.infodive_api.repository.AdminAutorizadoRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminAutorizadoServiceTest {

    @Mock
    private AdminAutorizadoRepository repository;

    @Mock
    private AdminAutorizadoMapper mapper;

    @InjectMocks
    private AdminAutorizadoService service;

    private AdminAutorizado admin;
    private AdminAutorizadoResponse adminResponse;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        admin = AdminAutorizado.builder()
                .id(id)
                .email("admin@infodive.com.br")
                .nome("Admin Infodive")
                .ativo(true)
                .build();

        adminResponse = new AdminAutorizadoResponse(id, "admin@infodive.com.br", "Admin Infodive", true, null);
    }

    @Test
    void isEmailAuthorized_WhenAuthorized_ShouldReturnTrue() {
        when(repository.findByEmailIgnoreCaseAndAtivoTrue("admin@infodive.com.br"))
                .thenReturn(Optional.of(admin));

        boolean authorized = service.isEmailAuthorized("admin@infodive.com.br");

        assertTrue(authorized);
    }

    @Test
    void isEmailAuthorized_WhenNullOrBlank_ShouldReturnFalse() {
        assertFalse(service.isEmailAuthorized(null));
        assertFalse(service.isEmailAuthorized("   "));
    }

    @Test
    void ensureEmailAuthorized_WhenNotPresent_ShouldSaveNewAdmin() {
        when(repository.findByEmailIgnoreCaseAndAtivoTrue("novo@infodive.com.br"))
                .thenReturn(Optional.empty());

        service.ensureEmailAuthorized("novo@infodive.com.br", "Novo Admin");

        verify(repository, times(1)).save(any(AdminAutorizado.class));
    }

    @Test
    void create_WhenEmailAlreadyExistsAndActive_ShouldThrowBusinessException() {
        AdminAutorizadoRequest request = new AdminAutorizadoRequest("admin@infodive.com.br", "Admin Infodive");
        when(repository.findByEmailAndAtivoTrue("admin@infodive.com.br")).thenReturn(Optional.of(admin));

        assertThrows(BusinessException.class, () -> service.create(request));
    }

    @Test
    void findById_WhenNotFound_ShouldThrowResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }

    @Test
    void delete_WhenExists_ShouldDeleteEntity() {
        UUID id = admin.getId();
        when(repository.findById(id)).thenReturn(Optional.of(admin));

        service.delete(id);

        verify(repository, times(1)).delete(admin);
    }
}
