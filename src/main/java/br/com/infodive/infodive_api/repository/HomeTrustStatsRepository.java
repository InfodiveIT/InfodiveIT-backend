package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.HomeTrustStats;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeTrustStatsRepository extends JpaRepository<HomeTrustStats, UUID> {

    List<HomeTrustStats> findAllByOrderByOrdemAsc();
}
