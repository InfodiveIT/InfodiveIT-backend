package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.ProdutoRequest;
import br.com.infodive.infodive_api.dto.response.ProdutoDetalheResponse;
import br.com.infodive.infodive_api.dto.response.ProdutoResumoResponse;
import br.com.infodive.infodive_api.service.ProdutoService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<Page<ProdutoResumoResponse>> findAll(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String fabricante,
            @RequestParam(required = false) Boolean destaque,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(produtoService.findAll(categoria, fabricante, destaque, page, size));
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<ProdutoDetalheResponse> findByIdentifier(@PathVariable String identifier) {
        try {
            UUID id = UUID.fromString(identifier);
            return ResponseEntity.ok(produtoService.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(produtoService.findBySlug(identifier));
        }
    }

    @PostMapping
    public ResponseEntity<ProdutoDetalheResponse> create(@Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDetalheResponse> update(
            @PathVariable UUID id, @Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.ok(produtoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        produtoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
