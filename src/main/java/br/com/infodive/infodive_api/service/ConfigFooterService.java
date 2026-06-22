package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.response.ConfigFooterResponse;
import br.com.infodive.infodive_api.entity.ConfigFooter;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.ConfigFooterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfigFooterService {

    private final ConfigFooterRepository configFooterRepository;

    @Transactional(readOnly = true)
    public ConfigFooterResponse get() {
        return configFooterRepository.findAll().stream().findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Configuração de footer não encontrada"));
    }

    private ConfigFooterResponse toResponse(ConfigFooter e) {
        return new ConfigFooterResponse(
                e.getDescricaoEmpresa(), e.getBadgeNoc(), e.getBadgeCloud(), e.getNomeLegal(),
                e.getUrlLinkedin(), e.getUrlInstagram(), e.getUrlFacebook());
    }
}
