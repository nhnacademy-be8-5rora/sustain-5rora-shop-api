package store.aurora.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.aurora.user.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
