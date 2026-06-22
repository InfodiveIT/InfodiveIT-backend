package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.response.HomeProblemasResponse;
import br.com.infodive.infodive_api.service.HomeProblemasService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home-problemas")
@RequiredArgsConstructor
public class HomeProblemasController {

    private final HomeProblemasService service;

    @GetMapping
    public ResponseEntity<List<HomeProblemasResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}
