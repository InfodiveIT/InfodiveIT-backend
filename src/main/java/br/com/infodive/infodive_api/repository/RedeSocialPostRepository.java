package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.RedeSocialPost;
import br.com.infodive.infodive_api.entity.RedeSocialPost.Rede;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedeSocialPostRepository extends JpaRepository<RedeSocialPost, UUID> {

    List<RedeSocialPost> findByAtivoTrueOrderByPublicadoEmDesc();

    List<RedeSocialPost> findByRedeAndAtivoTrueOrderByPublicadoEmDesc(Rede rede);

    Optional<RedeSocialPost> findByRedeAndExternalId(Rede rede, String externalId);
}
