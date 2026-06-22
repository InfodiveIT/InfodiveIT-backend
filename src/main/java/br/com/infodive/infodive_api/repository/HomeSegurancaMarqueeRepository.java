package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.HomeSegurancaMarquee;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeSegurancaMarqueeRepository extends JpaRepository<HomeSegurancaMarquee, UUID> {

    List<HomeSegurancaMarquee> findAllByAtivoTrueOrderByOrdemAsc();
}
