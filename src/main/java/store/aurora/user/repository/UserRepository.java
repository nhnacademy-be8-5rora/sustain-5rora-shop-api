package store.aurora.user.repository;

import feign.Param;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import store.aurora.user.entity.User;
import store.aurora.user.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @EntityGraph(attributePaths = {"orders", "orders.orderDetails"})
    @Query("SELECT u FROM User u")
    List<User> findAllWithOrders();
    List<User> findByLastLoginBeforeAndStatusNot(LocalDateTime lastLogin, UserStatus status);

    boolean existsById(String id);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);

    Object getUserById(@NotNull String id);

    //해당 달과 일치하는 생일을 가진 사용자 ID 검색
    @Query("SELECT u.id FROM User u WHERE MONTH(u.birth) = :month")
    List<String> findUserIdsByBirthMonth(@Param("month") int month);
}
