package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.ParceiroToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParceiroTokenRepository extends JpaRepository<ParceiroToken, UUID> {

    Optional<ParceiroToken> findByTokenAndAtivoTrue(String token);

    Optional<ParceiroToken> findByToken(String token);
}
