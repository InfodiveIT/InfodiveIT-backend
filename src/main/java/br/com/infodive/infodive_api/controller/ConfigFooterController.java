package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.response.ConfigFooterResponse;
import br.com.infodive.infodive_api.service.ConfigFooterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config-footer")
@RequiredArgsConstructor
public class ConfigFooterController {

    private final ConfigFooterService configFooterService;

    @GetMapping
    public ResponseEntity<ConfigFooterResponse> get() {
        return ResponseEntity.ok(configFooterService.get());
    }
}
