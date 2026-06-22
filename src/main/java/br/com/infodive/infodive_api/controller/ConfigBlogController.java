package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.response.ConfigBlogResponse;
import br.com.infodive.infodive_api.service.ConfigBlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config-blog")
@RequiredArgsConstructor
public class ConfigBlogController {

    private final ConfigBlogService configBlogService;

    @GetMapping
    public ResponseEntity<ConfigBlogResponse> get() {
        return ResponseEntity.ok(configBlogService.get());
    }
}
