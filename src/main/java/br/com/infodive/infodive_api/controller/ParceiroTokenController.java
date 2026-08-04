package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.ParceiroTokenRequest;
import br.com.infodive.infodive_api.dto.response.ParceiroTokenResponse;
import br.com.infodive.infodive_api.service.ParceiroTokenService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tokens-agencia")
@RequiredArgsConstructor
public class ParceiroTokenController {

    private final ParceiroTokenService service;

    @GetMapping
    public ResponseEntity<List<ParceiroTokenResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParceiroTokenResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ParceiroTokenResponse> create(@Valid @RequestBody ParceiroTokenRequest request, Principal principal) {
        String criadoPor = principal != null ? principal.getName() : "admin";
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, criadoPor));
    }

    @PatchMapping("/{id}/revogar")
    public ResponseEntity<ParceiroTokenResponse> revogar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.revogar(id));
    }

    @PatchMapping("/{id}/renovar")
    public ResponseEntity<ParceiroTokenResponse> renovar(@PathVariable UUID id, @RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(service.renovar(id, dias));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
