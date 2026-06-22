package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.AdminAutorizado;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminAutorizadoRepository extends JpaRepository<AdminAutorizado, UUID> {

    Optional<AdminAutorizado> findByEmailAndAtivoTrue(String email);
}
