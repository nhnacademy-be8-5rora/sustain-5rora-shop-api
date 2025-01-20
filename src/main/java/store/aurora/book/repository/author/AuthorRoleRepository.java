package store.aurora.book.repository.author;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.AuthorRole;

import java.util.Optional;

public interface AuthorRoleRepository extends JpaRepository<AuthorRole, Long> {
    boolean existsByRole(String role);
    Optional<AuthorRole> findByRole(String role);
    Page<AuthorRole> findAllByOrderById(Pageable pageable);
}

