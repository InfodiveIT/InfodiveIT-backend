package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.HeroHomeCarousel;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeroHomeCarouselRepository extends JpaRepository<HeroHomeCarousel, UUID> {

    List<HeroHomeCarousel> findAllByAtivoTrueOrderByOrdemAsc();
}
