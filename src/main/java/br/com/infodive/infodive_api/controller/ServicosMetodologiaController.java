package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.response.ServicosMetodologiaResponse;
import br.com.infodive.infodive_api.service.ServicosMetodologiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/servicos-metodologia")
@RequiredArgsConstructor
public class ServicosMetodologiaController {

    private final ServicosMetodologiaService service;

    @GetMapping
    public ResponseEntity<ServicosMetodologiaResponse> get() {
        return ResponseEntity.ok(service.get());
    }
}
