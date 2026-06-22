package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.LeadRequest;
import br.com.infodive.infodive_api.dto.response.LeadCreatedResponse;
import br.com.infodive.infodive_api.dto.response.LeadResponse;
import br.com.infodive.infodive_api.service.LeadService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    /** Captura pública de lead. */
    @PostMapping
    public ResponseEntity<LeadCreatedResponse> create(@Valid @RequestBody LeadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leadService.create(request));
    }

    /** Listagem para o admin (protegido por JWT na Fase 11). */
    @GetMapping
    public ResponseEntity<List<LeadResponse>> findAll() {
        return ResponseEntity.ok(leadService.findAll());
    }
}
