package store.aurora.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.user.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

}
