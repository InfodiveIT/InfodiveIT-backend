package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.ConfigFooter;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigFooterRepository extends JpaRepository<ConfigFooter, UUID> {
}
