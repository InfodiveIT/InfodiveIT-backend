package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.Cta;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtaRepository extends JpaRepository<Cta, UUID> {

    Optional<Cta> findByPagina(String pagina);
}
