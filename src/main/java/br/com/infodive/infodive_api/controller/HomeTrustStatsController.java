package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.HomeTrustStatsRequest;
import br.com.infodive.infodive_api.dto.response.HomeTrustStatsResponse;
import br.com.infodive.infodive_api.service.HomeTrustStatsService;
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
@RequestMapping("/home-trust-stats")
@RequiredArgsConstructor
public class HomeTrustStatsController {

    private final HomeTrustStatsService service;

    @GetMapping
    public ResponseEntity<List<HomeTrustStatsResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HomeTrustStatsResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<HomeTrustStatsResponse> create(@Valid @RequestBody HomeTrustStatsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HomeTrustStatsResponse> update(
            @PathVariable UUID id, @Valid @RequestBody HomeTrustStatsRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
