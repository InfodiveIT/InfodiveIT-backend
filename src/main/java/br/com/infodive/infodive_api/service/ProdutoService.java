package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.ProdutoRequest;
import br.com.infodive.infodive_api.dto.response.ProdutoDetalheResponse;
import br.com.infodive.infodive_api.dto.response.ProdutoResumoResponse;
import br.com.infodive.infodive_api.entity.Categoria;
import br.com.infodive.infodive_api.entity.Produto;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.ProdutoMapper;
import br.com.infodive.infodive_api.repository.CategoriaRepository;
import br.com.infodive.infodive_api.repository.FabricanteRepository;
import br.com.infodive.infodive_api.repository.ProdutoRepository;
import br.com.infodive.infodive_api.repository.ServicoRepository;
import br.com.infodive.infodive_api.repository.SolucaoRepository;
import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final FabricanteRepository fabricanteRepository;
    private final SolucaoRepository solucaoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ServicoRepository servicoRepository;
    private final ProdutoMapper produtoMapper;
    private final SupabaseStorageService supabaseStorageService;

    @Transactional(readOnly = true)
    public Page<ProdutoResumoResponse> findAll(
            String categoriaSlug, String fabricanteSlug, Boolean destaque, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return produtoRepository
                .findAllWithFilters(categoriaSlug, fabricanteSlug, destaque, pageable)
                .map(produtoMapper::toResumoResponse);
    }

    @Transactional(readOnly = true)
    public ProdutoDetalheResponse findBySlug(String slug) {
        return produtoRepository.findBySlugAndAtivoTrue(slug)
                .map(produtoMapper::toDetalheResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + slug));
    }

    @Transactional(readOnly = true)
    public ProdutoDetalheResponse findById(UUID id) {
        return produtoRepository.findById(id)
                .map(produtoMapper::toDetalheResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
    }

    @Transactional
    public ProdutoDetalheResponse create(ProdutoRequest request) {
        Produto produto = Produto.builder()
                .slug(request.slug())
                .nome(request.nome())
                .subcategoria(request.subcategoria())
                .descricaoCurta(request.descricaoCurta())
                .descricaoCompleta(request.descricaoCompleta())
                .diferenciais(request.diferenciais())
                .casosDeUso(request.casosDeUso())
                .servicosEyebrow(request.servicosEyebrow())
                .servicosTitulo(request.servicosTitulo())
                .servicosDescricao(request.servicosDescricao())
                .imagemUrl(request.imagemUrl())
                .destaque(request.destaque())
                .build();
        aplicarRelacionamentos(produto, request);
        return produtoMapper.toDetalheResponse(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoDetalheResponse update(UUID id, ProdutoRequest request) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
        
        String oldImagemUrl = produto.getImagemUrl();

        produto.setNome(request.nome());
        produto.setSubcategoria(request.subcategoria());
        produto.setDescricaoCurta(request.descricaoCurta());
        produto.setDescricaoCompleta(request.descricaoCompleta());
        produto.setDiferenciais(request.diferenciais());
        produto.setCasosDeUso(request.casosDeUso());
        produto.setServicosEyebrow(request.servicosEyebrow());
        produto.setServicosTitulo(request.servicosTitulo());
        produto.setServicosDescricao(request.servicosDescricao());
        produto.setImagemUrl(request.imagemUrl());
        produto.setDestaque(request.destaque());

        if (oldImagemUrl != null && !oldImagemUrl.equals(request.imagemUrl())) {
            supabaseStorageService.deleteFile(oldImagemUrl);
        }

        aplicarRelacionamentos(produto, request);
        return produtoMapper.toDetalheResponse(produtoRepository.save(produto));
    }

    @Transactional
    public void delete(UUID id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
        if (produto.getImagemUrl() != null) {
            supabaseStorageService.deleteFile(produto.getImagemUrl());
        }
        produtoRepository.delete(produto);
    }

    /** Resolve fabricante, solução, categoria e serviços a partir dos ids do request. */
    private void aplicarRelacionamentos(Produto produto, ProdutoRequest request) {
        if (request.fabricanteId() != null) {
            produto.setFabricante(fabricanteRepository.findById(request.fabricanteId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Fabricante não encontrado: " + request.fabricanteId())));
        } else {
            produto.setFabricante(null);
        }
        if (request.categoriaId() != null) {
            produto.setCategoria(categoriaRepository.findById(request.categoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Categoria não encontrada: " + request.categoriaId())));
        } else {
            produto.setCategoria(null);
        }
        if (request.solucaoId() != null) {
            produto.setSolucao(solucaoRepository.findById(request.solucaoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Solução não encontrada: " + request.solucaoId())));
        } else {
            produto.setSolucao(null);
        }
        produto.setServicos(request.servicoIds() == null || request.servicoIds().isEmpty()
                ? new ArrayList<>()
                : servicoRepository.findAllById(request.servicoIds()));
    }
}
