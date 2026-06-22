package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.ServicosMetodologia;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicosMetodologiaRepository extends JpaRepository<ServicosMetodologia, UUID> {
}
