package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.HomeSolucoesBento;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeSolucoesBentoRepository extends JpaRepository<HomeSolucoesBento, UUID> {

    List<HomeSolucoesBento> findAllByOrderByOrdemAsc();
}
