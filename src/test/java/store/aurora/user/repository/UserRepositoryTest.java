//package store.aurora.user.repository;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//import store.aurora.book.config.QuerydslConfiguration;
//import store.aurora.user.entity.User;
//import store.aurora.user.entity.UserStatus;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@Import(QuerydslConfiguration.class)
//@DataJpaTest
//class UserRepositoryTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    @DisplayName("ID로 사용자 존재 여부 확인")
//    void testExistsById() {
//        // Given
//        User user = new User("user1", LocalDate.of(1990, 1, 1), "1234567890", "user1@example.com", UserStatus.ACTIVE, LocalDateTime.now());
//        userRepository.save(user);
//
//        // When
//        boolean exists = userRepository.existsById("user1");
//
//        // Then
//        assertThat(exists).isTrue();
//    }
//
//    @Test
//    @DisplayName("전화번호로 사용자 존재 여부 확인")
//    void testExistsByPhoneNumber() {
//        // Given
//        User user = new User("user2", LocalDate.of(1991, 2, 2), "0987654321", "user2@example.com", UserStatus.ACTIVE, LocalDateTime.now());
//        userRepository.save(user);
//
//        // When
//        boolean exists = userRepository.existsByPhoneNumber("0987654321");
//
//        // Then
//        assertThat(exists).isTrue();
//    }
//
//    @Test
//    @DisplayName("이메일로 사용자 존재 여부 확인")
//    void testExistsByEmail() {
//        // Given
//        User user = new User("user3", LocalDate.of(1992, 3, 3), "1112233445", "user3@example.com", UserStatus.ACTIVE, LocalDateTime.now());
//        userRepository.save(user);
//
//        // When
//        boolean exists = userRepository.existsByEmail("user3@example.com");
//
//        // Then
//        assertThat(exists).isTrue();
//    }
//
//    @Test
//    @DisplayName("주어진 날짜 이전에 로그인한 사용자와 DELETED 상태가 아닌 사용자 조회")
//    void testFindByLastLoginBeforeAndStatusNot() {
//        // Given
//        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
//        User user1 = new User("user4", LocalDate.of(1993, 4, 4), "2223344556", "user4@example.com", UserStatus.ACTIVE, LocalDateTime.now().minusMonths(4));
//        User user2 = new User("user5", LocalDate.of(1994, 5, 5), "3334455667", "user5@example.com", UserStatus.DELETED, LocalDateTime.now().minusMonths(5));
//        userRepository.save(user1);
//        userRepository.save(user2);
//
//        // When
//        var users = userRepository.findByLastLoginBeforeAndStatusNot(threeMonthsAgo, UserStatus.DELETED);
//
//        // Then
//        assertThat(users).hasSize(1); // user2는 DELETED 상태여서 제외되어야 함
//        assertThat(users.get(0).getId()).isEqualTo("user4");
//    }
//}
