package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.ConfigBlog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigBlogRepository extends JpaRepository<ConfigBlog, UUID> {
}
