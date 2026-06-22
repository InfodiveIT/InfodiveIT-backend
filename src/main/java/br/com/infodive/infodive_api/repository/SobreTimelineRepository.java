package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.SobreTimeline;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SobreTimelineRepository extends JpaRepository<SobreTimeline, UUID> {
}
