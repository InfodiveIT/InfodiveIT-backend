package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.ContatoInfo;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContatoInfoRepository extends JpaRepository<ContatoInfo, UUID> {
}
