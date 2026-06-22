package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.HomeProblemas;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeProblemasRepository extends JpaRepository<HomeProblemas, UUID> {

    List<HomeProblemas> findAllByAtivoTrueOrderByOrdemAsc();
}
