package store.aurora.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.aurora.user.entity.User;
import store.aurora.user.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

//    Optional<User> findById(String id);

    List<User> findByLastLoginBeforeAndStatusNot(LocalDateTime lastLogin, UserStatus status);

    boolean existsById(String id);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
}
