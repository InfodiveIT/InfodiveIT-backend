package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.ConfigBlogRequest;
import br.com.infodive.infodive_api.dto.response.ConfigBlogResponse;
import br.com.infodive.infodive_api.entity.ConfigBlog;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.ConfigBlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfigBlogService {

    private final ConfigBlogRepository configBlogRepository;

    @Transactional
    public ConfigBlogResponse getOrCreate() {
        ConfigBlog entity = configBlogRepository.findAll().stream().findFirst()
                .orElseGet(() -> configBlogRepository.save(ConfigBlog.builder().build()));
        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public ConfigBlogResponse get() {
        return configBlogRepository.findAll().stream().findFirst()
                .map(this::toResponse)
                .orElseGet(() -> new ConfigBlogResponse(null, null, null, null, null, null, null, null, null, null, null));
    }

    @Transactional
    public ConfigBlogResponse update(ConfigBlogRequest request) {
        ConfigBlog entity = configBlogRepository.findAll().stream().findFirst()
                .orElseGet(() -> ConfigBlog.builder().build());
        entity.setArtigosEyebrow(request.artigosEyebrow());
        entity.setArtigosHeadline(request.artigosHeadline());
        entity.setSocialEyebrow(request.socialEyebrow());
        entity.setSocialHeadline(request.socialHeadline());
        entity.setSocialDescricao(request.socialDescricao());
        entity.setUrlInstagram(request.urlInstagram());
        entity.setUrlLinkedin(request.urlLinkedin());
        entity.setInstagramAccessToken(request.instagramAccessToken());
        entity.setInstagramAccountId(request.instagramAccountId());
        entity.setLinkedinAccessToken(request.linkedinAccessToken());
        entity.setLinkedinOrganizationId(request.linkedinOrganizationId());
        return toResponse(configBlogRepository.save(entity));
    }

    private ConfigBlogResponse toResponse(ConfigBlog e) {
        return new ConfigBlogResponse(
                e.getArtigosEyebrow(), e.getArtigosHeadline(), e.getSocialEyebrow(), e.getSocialHeadline(),
                e.getSocialDescricao(), e.getUrlInstagram(), e.getUrlLinkedin(),
                e.getInstagramAccessToken(), e.getInstagramAccountId(),
                e.getLinkedinAccessToken(), e.getLinkedinOrganizationId());
    }
}
