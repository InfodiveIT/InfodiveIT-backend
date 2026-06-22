package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.response.HomeSolucoesBentoResponse;
import br.com.infodive.infodive_api.service.HomeSolucoesBentoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home-solucoes-bento")
@RequiredArgsConstructor
public class HomeSolucoesBentoController {

    private final HomeSolucoesBentoService service;

    @GetMapping
    public ResponseEntity<List<HomeSolucoesBentoResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}
