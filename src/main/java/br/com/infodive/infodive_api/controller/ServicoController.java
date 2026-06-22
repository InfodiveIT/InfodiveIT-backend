package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.ServicoRequest;
import br.com.infodive.infodive_api.dto.response.ServicoResponse;
import br.com.infodive.infodive_api.service.ServicoService;
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
@RequestMapping("/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService servicoService;

    @GetMapping
    public ResponseEntity<List<ServicoResponse>> findAll() {
        return ResponseEntity.ok(servicoService.findAll());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ServicoResponse> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(servicoService.findBySlug(slug));
    }

    @PostMapping
    public ResponseEntity<ServicoResponse> create(@Valid @RequestBody ServicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponse> update(
            @PathVariable UUID id, @Valid @RequestBody ServicoRequest request) {
        return ResponseEntity.ok(servicoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        servicoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
