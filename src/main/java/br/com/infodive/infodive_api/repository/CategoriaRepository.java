package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.Categoria;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {
    List<Categoria> findAllByAtivoTrueOrderByOrdemAscNomeAsc();
    Optional<Categoria> findBySlugAndAtivoTrue(String slug);
}
