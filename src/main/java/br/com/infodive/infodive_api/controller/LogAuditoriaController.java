package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.response.LogAuditoriaResponse;
import br.com.infodive.infodive_api.service.LogAuditoriaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs-auditoria")
@RequiredArgsConstructor
public class LogAuditoriaController {

    private final LogAuditoriaService service;

    @GetMapping
    public ResponseEntity<List<LogAuditoriaResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}
