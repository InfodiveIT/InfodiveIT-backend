package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.ConteudoRequest;
import br.com.infodive.infodive_api.dto.response.ConteudoResponse;
import br.com.infodive.infodive_api.entity.Conteudo;
import br.com.infodive.infodive_api.entity.OrigemConteudo;
import br.com.infodive.infodive_api.entity.TipoConteudo;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.ConteudoMapper;
import br.com.infodive.infodive_api.repository.ConteudoRepository;
import br.com.infodive.infodive_api.repository.FabricanteRepository;
import br.com.infodive.infodive_api.repository.ProdutoRepository;
import br.com.infodive.infodive_api.repository.SolucaoRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConteudoService {

    private final ConteudoRepository conteudoRepository;
    private final SolucaoRepository solucaoRepository;
    private final FabricanteRepository fabricanteRepository;
    private final ProdutoRepository produtoRepository;
    private final ConteudoMapper conteudoMapper;

    @Transactional(readOnly = true)
    public Page<ConteudoResponse> findAll(TipoConteudo tipo, OrigemConteudo origem, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return conteudoRepository.findAllWithFilters(tipo, origem, pageable)
                .map(conteudoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ConteudoResponse findBySlug(String slug) {
        return conteudoRepository.findBySlugAndAtivoTrue(slug)
                .map(conteudoMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Conteúdo não encontrado: " + slug));
    }

    @Transactional
    public ConteudoResponse create(ConteudoRequest request) {
        Conteudo conteudo = Conteudo.builder()
                .titulo(request.titulo())
                .slug(request.slug())
                .tipo(request.tipo())
                .origem(request.origem())
                .descricao(request.descricao())
                .conteudo(request.conteudo())
                .imagemUrl(request.imagemUrl())
                .urlExterna(request.urlExterna())
                .socialPostId(request.socialPostId())
                .autor(request.autor())
                .tempoLeitura(request.tempoLeitura())
                .publicadoEm(request.publicadoEm())
                .build();
        aplicarRelacionamentos(conteudo, request);
        return conteudoMapper.toResponse(conteudoRepository.save(conteudo));
    }

    @Transactional
    public ConteudoResponse update(UUID id, ConteudoRequest request) {
        Conteudo conteudo = conteudoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conteúdo não encontrado: " + id));
        conteudo.setTitulo(request.titulo());
        conteudo.setSlug(request.slug());
        conteudo.setTipo(request.tipo());
        conteudo.setOrigem(request.origem());
        conteudo.setDescricao(request.descricao());
        conteudo.setConteudo(request.conteudo());
        conteudo.setImagemUrl(request.imagemUrl());
        conteudo.setUrlExterna(request.urlExterna());
        conteudo.setSocialPostId(request.socialPostId());
        conteudo.setAutor(request.autor());
        conteudo.setTempoLeitura(request.tempoLeitura());
        conteudo.setPublicadoEm(request.publicadoEm());
        aplicarRelacionamentos(conteudo, request);
        return conteudoMapper.toResponse(conteudoRepository.save(conteudo));
    }

    @Transactional
    public void delete(UUID id) {
        Conteudo conteudo = conteudoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conteúdo não encontrado: " + id));
        conteudo.setAtivo(false);
        conteudoRepository.save(conteudo);
    }

    private void aplicarRelacionamentos(Conteudo conteudo, ConteudoRequest request) {
        conteudo.setCategoria(request.categoriaId() == null ? null
                : solucaoRepository.findById(request.categoriaId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Categoria não encontrada: " + request.categoriaId())));
        conteudo.setFabricante(request.fabricanteId() == null ? null
                : fabricanteRepository.findById(request.fabricanteId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Fabricante não encontrado: " + request.fabricanteId())));
        conteudo.setProduto(request.produtoId() == null ? null
                : produtoRepository.findById(request.produtoId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Produto não encontrado: " + request.produtoId())));
    }
}
