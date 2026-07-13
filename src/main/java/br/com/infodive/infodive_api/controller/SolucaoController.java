package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.SolucaoRequest;
import br.com.infodive.infodive_api.dto.response.SolucaoResponse;
import br.com.infodive.infodive_api.service.SolucaoService;
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
@RequestMapping("/solucoes")
@RequiredArgsConstructor
public class SolucaoController {

    private final SolucaoService service;

    @GetMapping
    public ResponseEntity<List<SolucaoResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<SolucaoResponse> findByIdentifier(@PathVariable String identifier) {
        try {
            UUID id = UUID.fromString(identifier);
            return ResponseEntity.ok(service.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(service.findBySlug(identifier));
        }
    }

    @PostMapping
    public ResponseEntity<SolucaoResponse> create(@Valid @RequestBody SolucaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolucaoResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody SolucaoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
