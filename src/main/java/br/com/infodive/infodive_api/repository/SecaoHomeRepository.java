package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.SecaoHome;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecaoHomeRepository extends JpaRepository<SecaoHome, UUID> {

    Optional<SecaoHome> findBySecao(String secao);
}
