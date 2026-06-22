package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.Case;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseRepository extends JpaRepository<Case, UUID> {

    List<Case> findAllByAtivoTrueOrderByOrdemAsc();
}
