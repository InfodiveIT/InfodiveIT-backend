package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.response.ServicosEtapasResponse;
import br.com.infodive.infodive_api.service.ServicosEtapasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/servicos-etapas")
@RequiredArgsConstructor
public class ServicosEtapasController {

    private final ServicosEtapasService service;

    @GetMapping
    public ResponseEntity<ServicosEtapasResponse> get() {
        return ResponseEntity.ok(service.get());
    }
}
