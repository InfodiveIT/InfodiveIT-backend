package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.Servico;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoRepository extends JpaRepository<Servico, UUID> {

    List<Servico> findAllByAtivoTrueOrderByOrdemAscNomeAsc();

    Optional<Servico> findBySlugAndAtivoTrue(String slug);
}
