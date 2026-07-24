package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.LogAuditoria;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, UUID> {

    List<LogAuditoria> findAllByOrderByCriadoEmDesc();

    Page<LogAuditoria> findAllByOrderByCriadoEmDesc(Pageable pageable);
}
