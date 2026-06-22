package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.response.FaqResponse;
import br.com.infodive.infodive_api.service.FaqService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @GetMapping
    public ResponseEntity<List<FaqResponse>> findAll() {
        return ResponseEntity.ok(faqService.findAll());
    }
}
