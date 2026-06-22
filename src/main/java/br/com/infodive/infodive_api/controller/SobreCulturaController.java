package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.response.SobreCulturaResponse;
import br.com.infodive.infodive_api.service.SobreCulturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sobre-cultura")
@RequiredArgsConstructor
public class SobreCulturaController {

    private final SobreCulturaService service;

    @GetMapping
    public ResponseEntity<SobreCulturaResponse> get() {
        return ResponseEntity.ok(service.get());
    }
}
