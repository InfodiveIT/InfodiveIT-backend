package br.com.infodive.infodive_api.service;

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

    @Transactional(readOnly = true)
    public ConfigBlogResponse get() {
        return configBlogRepository.findAll().stream().findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Configuração de blog não encontrada"));
    }

    private ConfigBlogResponse toResponse(ConfigBlog e) {
        return new ConfigBlogResponse(
                e.getArtigosEyebrow(), e.getArtigosHeadline(), e.getSocialEyebrow(), e.getSocialHeadline(),
                e.getSocialDescricao(), e.getUrlInstagram(), e.getUrlLinkedin());
    }
}
