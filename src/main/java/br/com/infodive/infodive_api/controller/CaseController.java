package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.CaseRequest;
import br.com.infodive.infodive_api.dto.response.CaseResponse;
import br.com.infodive.infodive_api.service.CaseService;
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
@RequestMapping("/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    @GetMapping
    public ResponseEntity<List<CaseResponse>> findAll() {
        return ResponseEntity.ok(caseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaseResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(caseService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CaseResponse> create(@Valid @RequestBody CaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(caseService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaseResponse> update(
            @PathVariable UUID id, @Valid @RequestBody CaseRequest request) {
        return ResponseEntity.ok(caseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        caseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
