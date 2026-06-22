package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.Conteudo;
import br.com.infodive.infodive_api.entity.OrigemConteudo;
import br.com.infodive.infodive_api.entity.TipoConteudo;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConteudoRepository extends JpaRepository<Conteudo, UUID> {

    @Query("""
            SELECT c FROM Conteudo c
            WHERE c.ativo = true
            AND (:tipo IS NULL OR c.tipo = :tipo)
            AND (:origem IS NULL OR c.origem = :origem)
            ORDER BY c.publicadoEm DESC NULLS LAST, c.createdAt DESC
            """)
    Page<Conteudo> findAllWithFilters(
            @Param("tipo") TipoConteudo tipo,
            @Param("origem") OrigemConteudo origem,
            Pageable pageable
    );

    Optional<Conteudo> findBySlugAndAtivoTrue(String slug);
}
