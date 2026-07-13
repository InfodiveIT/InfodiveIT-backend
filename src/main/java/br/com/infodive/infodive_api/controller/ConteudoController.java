package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.ConteudoRequest;
import br.com.infodive.infodive_api.dto.response.ConteudoResponse;
import br.com.infodive.infodive_api.entity.OrigemConteudo;
import br.com.infodive.infodive_api.entity.TipoConteudo;
import br.com.infodive.infodive_api.service.ConteudoService;
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
@RequestMapping("/conteudos")
@RequiredArgsConstructor
public class ConteudoController {

    private final ConteudoService conteudoService;

    @GetMapping
    public ResponseEntity<Page<ConteudoResponse>> findAll(
            @RequestParam(required = false) TipoConteudo tipo,
            @RequestParam(required = false) OrigemConteudo origem,
            @RequestParam(required = false) Boolean destaque,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        return ResponseEntity.ok(conteudoService.findAll(tipo, origem, destaque, page, size));
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<ConteudoResponse> findByIdentifier(@PathVariable String identifier) {
        try {
            UUID id = UUID.fromString(identifier);
            return ResponseEntity.ok(conteudoService.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(conteudoService.findBySlug(identifier));
        }
    }

    @PostMapping
    public ResponseEntity<ConteudoResponse> create(@Valid @RequestBody ConteudoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conteudoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConteudoResponse> update(
            @PathVariable UUID id, @Valid @RequestBody ConteudoRequest request) {
        return ResponseEntity.ok(conteudoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        conteudoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
