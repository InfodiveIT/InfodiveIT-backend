package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.Produto;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

    // Listagem com filtros opcionais — página /produtos
    @Query("""
            SELECT p FROM Produto p
            WHERE p.ativo = true
            AND (:categoriaSlug IS NULL OR p.solucao.slug = :categoriaSlug)
            AND (:fabricanteSlug IS NULL OR p.fabricante.slug = :fabricanteSlug)
            AND (:destaque IS NULL OR p.destaque = :destaque)
            ORDER BY p.destaque DESC, p.nome ASC
            """)
    Page<Produto> findAllWithFilters(
            @Param("categoriaSlug") String categoriaSlug,
            @Param("fabricanteSlug") String fabricanteSlug,
            @Param("destaque") Boolean destaque,
            Pageable pageable
    );

    Optional<Produto> findBySlugAndAtivoTrue(String slug);
}
