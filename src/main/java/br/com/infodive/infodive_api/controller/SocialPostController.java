package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.RedeSocialPostDTO;
import br.com.infodive.infodive_api.entity.RedeSocialPost.Rede;
import br.com.infodive.infodive_api.repository.RedeSocialPostRepository;
import br.com.infodive.infodive_api.service.SocialMediaSyncService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/social-posts")
@RequiredArgsConstructor
public class SocialPostController {

    private final RedeSocialPostRepository redeSocialPostRepository;
    private final SocialMediaSyncService socialMediaSyncService;

    @GetMapping
    public ResponseEntity<List<RedeSocialPostDTO>> list(
            @RequestParam(name = "rede", required = false) Rede rede
    ) {
        List<RedeSocialPostDTO> posts;
        if (rede != null) {
            posts = redeSocialPostRepository.findByRedeAndAtivoTrueOrderByPublicadoEmDesc(rede)
                    .stream()
                    .map(RedeSocialPostDTO::fromEntity)
                    .toList();
        } else {
            posts = redeSocialPostRepository.findByAtivoTrueOrderByPublicadoEmDesc()
                    .stream()
                    .map(RedeSocialPostDTO::fromEntity)
                    .toList();
        }
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> sync() {
        var syncedPosts = socialMediaSyncService.syncAll();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Sincronização de redes sociais concluída com sucesso.",
                "totalSynced", syncedPosts.size()
        ));
    }
}
