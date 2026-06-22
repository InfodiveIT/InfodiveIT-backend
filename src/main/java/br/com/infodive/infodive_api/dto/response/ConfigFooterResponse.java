package br.com.infodive.infodive_api.dto.response;

public record ConfigFooterResponse(
        String descricaoEmpresa,
        String badgeNoc,
        String badgeCloud,
        String nomeLegal,
        String urlLinkedin,
        String urlInstagram,
        String urlFacebook
) {}
