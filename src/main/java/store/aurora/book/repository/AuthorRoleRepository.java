package store.aurora.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.AuthorRole;

public interface AuthorRoleRepository extends JpaRepository<AuthorRole, Long> {}

