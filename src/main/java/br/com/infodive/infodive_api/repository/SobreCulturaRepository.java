package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.SobreCultura;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SobreCulturaRepository extends JpaRepository<SobreCultura, UUID> {
}
