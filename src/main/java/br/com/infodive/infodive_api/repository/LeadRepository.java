package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.Lead;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadRepository extends JpaRepository<Lead, UUID> {

    List<Lead> findAllByOrderByCriadoEmDesc();
}
