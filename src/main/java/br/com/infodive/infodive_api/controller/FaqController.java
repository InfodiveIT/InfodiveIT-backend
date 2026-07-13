package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.FaqRequest;
import br.com.infodive.infodive_api.dto.response.FaqResponse;
import br.com.infodive.infodive_api.service.FaqService;
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
@RequestMapping("/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @GetMapping
    public ResponseEntity<List<FaqResponse>> findAll() {
        return ResponseEntity.ok(faqService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FaqResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(faqService.findById(id));
    }

    @PostMapping
    public ResponseEntity<FaqResponse> create(@Valid @RequestBody FaqRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(faqService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FaqResponse> update(
            @PathVariable UUID id, @Valid @RequestBody FaqRequest request) {
        return ResponseEntity.ok(faqService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        faqService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
