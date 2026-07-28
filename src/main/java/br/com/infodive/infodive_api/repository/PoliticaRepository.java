package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.Politica;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoliticaRepository extends JpaRepository<Politica, UUID> {

    Optional<Politica> findBySlugAndAtivoTrue(String slug);

    Optional<Politica> findBySlug(String slug);

    List<Politica> findAllByAtivoTrueOrderByTituloAsc();

    List<Politica> findAllByOrderByTituloAsc();
}
