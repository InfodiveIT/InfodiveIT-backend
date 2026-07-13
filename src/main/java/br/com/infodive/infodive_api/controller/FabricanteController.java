
package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.FabricanteRequest;
import br.com.infodive.infodive_api.dto.response.FabricanteResponse;
import br.com.infodive.infodive_api.service.FabricanteService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fabricantes")
@RequiredArgsConstructor
public class FabricanteController {

    private final FabricanteService fabricanteService;

    @GetMapping
    public ResponseEntity<List<FabricanteResponse>> findAll(
            @RequestParam(required = false) Boolean destaque) {
        return ResponseEntity.ok(fabricanteService.findAll(destaque));
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<FabricanteResponse> findByIdentifier(@PathVariable String identifier) {
        try {
            UUID id = UUID.fromString(identifier);
            return ResponseEntity.ok(fabricanteService.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(fabricanteService.findBySlug(identifier));
        }
    }

    @PostMapping
    public ResponseEntity<FabricanteResponse> create(@Valid @RequestBody FabricanteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fabricanteService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FabricanteResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody FabricanteRequest request) {
        return ResponseEntity.ok(fabricanteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        fabricanteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
