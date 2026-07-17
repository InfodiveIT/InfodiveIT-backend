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
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.infodive.infodive_api.entity.ConteudoBloco;
import java.util.List;
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
    private final ObjectMapper objectMapper;
    private final SupabaseStorageService supabaseStorageService;

    @Transactional(readOnly = true)
    public Page<ConteudoResponse> findAll(TipoConteudo tipo, OrigemConteudo origem, Boolean destaque, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return conteudoRepository.findAllWithFilters(tipo, origem, destaque, pageable)
                .map(conteudoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ConteudoResponse findBySlug(String slug) {
        return conteudoRepository.findBySlugAndAtivoTrue(slug)
                .map(conteudoMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Conteúdo não encontrado: " + slug));
    }

    @Transactional(readOnly = true)
    public ConteudoResponse findById(UUID id) {
        return conteudoRepository.findById(id)
                .map(conteudoMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Conteúdo não encontrado: " + id));
    }

    @Transactional
    public ConteudoResponse create(ConteudoRequest request) {
        validarDestaques(null, request.destaque(), true);
        Conteudo conteudo = Conteudo.builder()
                .titulo(request.titulo())
                .slug(request.slug())
                .tipo(request.tipo())
                .origem(request.origem())
                .descricao(request.descricao())
                .conteudo(parseConteudo(request.conteudo()))
                .imagemUrl(request.imagemUrl())
                .urlExterna(request.urlExterna())
                .socialPostId(request.socialPostId())
                .autor(request.autor())
                .tempoLeitura(request.tempoLeitura())
                .publicadoEm(request.publicadoEm())
                .destaque(request.destaque())
                .build();
        aplicarRelacionamentos(conteudo, request);
        return conteudoMapper.toResponse(conteudoRepository.save(conteudo));
    }

    @Transactional
    public ConteudoResponse update(UUID id, ConteudoRequest request) {
        Conteudo conteudo = conteudoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conteúdo não encontrado: " + id));
        validarDestaques(id, request.destaque(), conteudo.isAtivo());
        
        String oldImagemUrl = conteudo.getImagemUrl();

        conteudo.setTitulo(request.titulo());
        conteudo.setSlug(request.slug());
        conteudo.setTipo(request.tipo());
        conteudo.setOrigem(request.origem());
        conteudo.setDescricao(request.descricao());
        conteudo.setConteudo(parseConteudo(request.conteudo()));
        conteudo.setImagemUrl(request.imagemUrl());
        conteudo.setUrlExterna(request.urlExterna());
        conteudo.setSocialPostId(request.socialPostId());
        conteudo.setAutor(request.autor());
        conteudo.setTempoLeitura(request.tempoLeitura());
        conteudo.setPublicadoEm(request.publicadoEm());
        conteudo.setDestaque(request.destaque());

        if (oldImagemUrl != null && !oldImagemUrl.equals(request.imagemUrl())) {
            supabaseStorageService.deleteFile(oldImagemUrl);
        }

        aplicarRelacionamentos(conteudo, request);
        return conteudoMapper.toResponse(conteudoRepository.save(conteudo));
    }

    @Transactional
    public void delete(UUID id) {
        Conteudo conteudo = conteudoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conteúdo não encontrado: " + id));
        if (conteudo.isDestaque() && conteudo.isAtivo()) {
            validarDestaques(id, false, false);
        }
        if (conteudo.getImagemUrl() != null) {
            supabaseStorageService.deleteFile(conteudo.getImagemUrl());
        }
        conteudoRepository.delete(conteudo);
    }

    private void validarDestaques(UUID idSendoModificado, boolean novoDestaque, boolean novoAtivo) {
        // Conta quantos conteúdos destacados ativos existem no banco
        long count = conteudoRepository.countByAtivoTrueAndDestaqueTrue();
        
        // Verifica se o item atual já é um destaque ativo
        boolean jaEraDestaqueAtivo = false;
        if (idSendoModificado != null) {
            jaEraDestaqueAtivo = conteudoRepository.findById(idSendoModificado)
                    .map(c -> c.isAtivo() && c.isDestaque())
                    .orElse(false);
        }
        
        // Calcula a quantidade projetada de destaques ativos
        long projetado = count;
        if (jaEraDestaqueAtivo) {
            if (!novoDestaque || !novoAtivo) {
                projetado--;
            }
        } else {
            if (novoDestaque && novoAtivo) {
                projetado++;
            }
        }
        
        if (projetado < 1) {
            throw new br.com.infodive.infodive_api.exception.BusinessException("Deve haver no mínimo 1 artigo em destaque na página inicial.");
        }
        if (projetado > 3) {
            throw new br.com.infodive.infodive_api.exception.BusinessException("Não é permitido destacar mais de 3 artigos na página inicial.");
        }
    }

    private List<ConteudoBloco> parseConteudo(Object conteudoObj) {
        if (conteudoObj == null) {
            return null;
        }
        try {
            if (conteudoObj instanceof String str) {
                if (str.isBlank()) return null;
                return objectMapper.readValue(str, new com.fasterxml.jackson.core.type.TypeReference<List<ConteudoBloco>>() {});
            }
            return objectMapper.convertValue(conteudoObj, new com.fasterxml.jackson.core.type.TypeReference<List<ConteudoBloco>>() {});
        } catch (Exception e) {
            throw new br.com.infodive.infodive_api.exception.BusinessException("Formato de conteúdo inválido: " + e.getMessage());
        }
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
