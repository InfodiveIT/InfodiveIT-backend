package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.AdminAutorizadoRequest;
import br.com.infodive.infodive_api.dto.response.AdminAutorizadoResponse;
import br.com.infodive.infodive_api.service.AdminAutorizadoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admins-autorizados")
@RequiredArgsConstructor
public class AdminAutorizadoController {

    private final AdminAutorizadoService service;

    @GetMapping
    public ResponseEntity<List<AdminAutorizadoResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminAutorizadoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<AdminAutorizadoResponse> create(@Valid @RequestBody AdminAutorizadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminAutorizadoResponse> update(
            @PathVariable UUID id, @Valid @RequestBody AdminAutorizadoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
