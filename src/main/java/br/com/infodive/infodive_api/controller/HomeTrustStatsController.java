package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.response.HomeTrustStatsResponse;
import br.com.infodive.infodive_api.service.HomeTrustStatsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home-trust-stats")
@RequiredArgsConstructor
public class HomeTrustStatsController {

    private final HomeTrustStatsService service;

    @GetMapping
    public ResponseEntity<List<HomeTrustStatsResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}
