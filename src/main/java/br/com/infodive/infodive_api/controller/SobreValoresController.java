package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.response.SobreValoresResponse;
import br.com.infodive.infodive_api.service.SobreValoresService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sobre-valores")
@RequiredArgsConstructor
public class SobreValoresController {

    private final SobreValoresService service;

    @GetMapping
    public ResponseEntity<SobreValoresResponse> get() {
        return ResponseEntity.ok(service.get());
    }
}
