package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.CtaRequest;
import br.com.infodive.infodive_api.dto.response.CtaResponse;
import br.com.infodive.infodive_api.service.CtaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ctas")
@RequiredArgsConstructor
public class CtaController {

    private final CtaService ctaService;

    @GetMapping
    public ResponseEntity<java.util.List<CtaResponse>> findAll() {
        return ResponseEntity.ok(ctaService.findAll());
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<CtaResponse> findByIdentifier(@PathVariable String identifier) {
        return ResponseEntity.ok(ctaService.findByIdentifier(identifier));
    }

    @PutMapping("/{identifier}")
    public ResponseEntity<CtaResponse> update(
            @PathVariable String identifier, @Valid @RequestBody CtaRequest request) {
        return ResponseEntity.ok(ctaService.update(identifier, request));
    }

}
