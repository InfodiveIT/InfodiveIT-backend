package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.SobreNumeros;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SobreNumerosRepository extends JpaRepository<SobreNumeros, UUID> {
}
