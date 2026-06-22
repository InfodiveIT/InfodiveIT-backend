package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.Faq;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<Faq, UUID> {

    List<Faq> findAllByAtivoTrueOrderByOrdemAsc();
}
