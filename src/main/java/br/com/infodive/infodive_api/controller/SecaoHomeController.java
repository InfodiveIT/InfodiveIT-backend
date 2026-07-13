package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.SecaoHomeRequest;
import br.com.infodive.infodive_api.dto.response.SecaoHomeResponse;
import br.com.infodive.infodive_api.service.SecaoHomeService;
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
@RequestMapping("/secoes-home")
@RequiredArgsConstructor
public class SecaoHomeController {

    private final SecaoHomeService secaoHomeService;

    @GetMapping
    public ResponseEntity<java.util.List<SecaoHomeResponse>> findAll() {
        return ResponseEntity.ok(secaoHomeService.findAll());
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<SecaoHomeResponse> findByIdentifier(@PathVariable String identifier) {
        return ResponseEntity.ok(secaoHomeService.findByIdentifier(identifier));
    }

    @PutMapping("/{identifier}")
    public ResponseEntity<SecaoHomeResponse> update(
            @PathVariable String identifier, @Valid @RequestBody SecaoHomeRequest request) {
        return ResponseEntity.ok(secaoHomeService.update(identifier, request));
    }

}
