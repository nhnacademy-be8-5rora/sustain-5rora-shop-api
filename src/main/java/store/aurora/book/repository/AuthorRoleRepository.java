package store.aurora.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.AuthorRole;

import java.util.Optional;

public interface AuthorRoleRepository extends JpaRepository<AuthorRole, Long> {
    Optional<AuthorRole> findByRole(AuthorRole.Role role);
    boolean existsByRole(AuthorRole.Role role);
}

