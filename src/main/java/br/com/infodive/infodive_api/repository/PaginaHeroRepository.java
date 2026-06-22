package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.PaginaHero;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaginaHeroRepository extends JpaRepository<PaginaHero, UUID> {

    Optional<PaginaHero> findByPagina(String pagina);
}
