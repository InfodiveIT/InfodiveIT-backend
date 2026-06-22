package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.response.ContatoInfoResponse;
import br.com.infodive.infodive_api.service.ContatoInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contato-info")
@RequiredArgsConstructor
public class ContatoInfoController {

    private final ContatoInfoService contatoInfoService;

    @GetMapping
    public ResponseEntity<ContatoInfoResponse> get() {
        return ResponseEntity.ok(contatoInfoService.get());
    }
}
