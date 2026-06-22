package br.com.infodive.infodive_api.mapper;

import br.com.infodive.infodive_api.dto.request.CaseRequest;
import br.com.infodive.infodive_api.dto.response.CaseResponse;
import br.com.infodive.infodive_api.entity.Case;
import org.springframework.stereotype.Component;

@Component
public class CaseMapper {

    public CaseResponse toResponse(Case entity) {
        return new CaseResponse(
                entity.getId(),
                entity.getSegmento(),
                entity.getCliente(),
                entity.getTitulo(),
                entity.getDesafio(),
                entity.getResultado(),
                entity.getMetrica(),
                entity.getAutor(),
                entity.getCargo(),
                entity.getDepoimento(),
                entity.getImagemUrl(),
                entity.getOrdem()
        );
    }

    public Case toEntity(CaseRequest request) {
        return Case.builder()
                .segmento(request.segmento())
                .cliente(request.cliente())
                .titulo(request.titulo())
                .desafio(request.desafio())
                .resultado(request.resultado())
                .metrica(request.metrica())
                .autor(request.autor())
                .cargo(request.cargo())
                .depoimento(request.depoimento())
                .imagemUrl(request.imagemUrl())
                .ordem(request.ordem())
                .build();
    }

    public void updateEntity(Case entity, CaseRequest request) {
        entity.setSegmento(request.segmento());
        entity.setCliente(request.cliente());
        entity.setTitulo(request.titulo());
        entity.setDesafio(request.desafio());
        entity.setResultado(request.resultado());
        entity.setMetrica(request.metrica());
        entity.setAutor(request.autor());
        entity.setCargo(request.cargo());
        entity.setDepoimento(request.depoimento());
        entity.setImagemUrl(request.imagemUrl());
        entity.setOrdem(request.ordem());
    }
}
