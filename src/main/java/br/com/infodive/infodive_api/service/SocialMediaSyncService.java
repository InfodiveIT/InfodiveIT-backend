package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.entity.ConfigBlog;
import br.com.infodive.infodive_api.entity.RedeSocialPost;
import br.com.infodive.infodive_api.entity.RedeSocialPost.Rede;
import br.com.infodive.infodive_api.repository.ConfigBlogRepository;
import br.com.infodive.infodive_api.repository.RedeSocialPostRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialMediaSyncService {

    private final InstagramApiService instagramApiService;
    private final LinkedinApiService linkedinApiService;
    private final RedeSocialPostRepository redeSocialPostRepository;
    private final ConfigBlogRepository configBlogRepository;

    @Transactional
    public List<RedeSocialPost> syncAll() {
        log.info("Iniciando sincronização de posts de redes sociais (Instagram & LinkedIn)...");

        ConfigBlog config = configBlogRepository.findAll().stream().findFirst().orElse(null);

        String igToken = config != null ? config.getInstagramAccessToken() : null;
        String igAccount = config != null ? config.getInstagramAccountId() : null;
        String liToken = config != null ? config.getLinkedinAccessToken() : null;
        String liOrg = config != null ? config.getLinkedinOrganizationId() : null;

        List<RedeSocialPost> igPosts = instagramApiService.fetchRecentPosts(igToken, igAccount);
        List<RedeSocialPost> liPosts = linkedinApiService.fetchRecentPosts(liToken, liOrg);

        List<RedeSocialPost> savedPosts = new ArrayList<>();

        for (RedeSocialPost fetched : igPosts) {
            savedPosts.add(saveOrUpdate(fetched));
        }

        for (RedeSocialPost fetched : liPosts) {
            savedPosts.add(saveOrUpdate(fetched));
        }

        log.info("Sincronização concluída. Total de posts salvos/atualizados: {}", savedPosts.size());
        return savedPosts;
    }

    private RedeSocialPost saveOrUpdate(RedeSocialPost fetched) {
        Optional<RedeSocialPost> existingOpt = redeSocialPostRepository.findByRedeAndExternalId(
                fetched.getRede(), fetched.getExternalId());

        if (existingOpt.isPresent()) {
            RedeSocialPost existing = existingOpt.get();
            existing.setTextoLegenda(fetched.getTextoLegenda());
            existing.setImagemUrl(fetched.getImagemUrl());
            existing.setPermalinkUrl(fetched.getPermalinkUrl());
            existing.setLikesCount(fetched.getLikesCount());
            existing.setCommentsCount(fetched.getCommentsCount());
            existing.setPublicadoEm(fetched.getPublicadoEm());
            existing.setAtivo(true);
            return redeSocialPostRepository.save(existing);
        } else {
            return redeSocialPostRepository.save(fetched);
        }
    }
}
