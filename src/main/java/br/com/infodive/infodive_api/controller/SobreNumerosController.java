package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.response.SobreNumerosResponse;
import br.com.infodive.infodive_api.service.SobreNumerosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sobre-numeros")
@RequiredArgsConstructor
public class SobreNumerosController {

    private final SobreNumerosService service;

    @GetMapping
    public ResponseEntity<SobreNumerosResponse> get() {
        return ResponseEntity.ok(service.get());
    }
}
