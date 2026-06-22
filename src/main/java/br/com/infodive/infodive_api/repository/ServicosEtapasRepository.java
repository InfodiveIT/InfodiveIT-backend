package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.ServicosEtapas;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicosEtapasRepository extends JpaRepository<ServicosEtapas, UUID> {
}
