package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.response.HomeSegurancaMarqueeResponse;
import br.com.infodive.infodive_api.service.HomeSegurancaMarqueeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home-seguranca-marquee")
@RequiredArgsConstructor
public class HomeSegurancaMarqueeController {

    private final HomeSegurancaMarqueeService service;

    @GetMapping
    public ResponseEntity<List<HomeSegurancaMarqueeResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}
