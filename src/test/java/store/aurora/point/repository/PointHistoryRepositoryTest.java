package store.aurora.point.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.point.entity.PointHistory;
import store.aurora.point.entity.PointPolicy;
import store.aurora.point.entity.PointPolicyType;
import store.aurora.point.entity.PointType;
import store.aurora.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(QuerydslConfiguration.class)
@DataJpaTest
class PointHistoryRepositoryTest {

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        // Given
        user = new User("user1", "John Doe", LocalDate.of(1990, 1, 1), "010-1234-5678", "test@example.com", false);
        entityManager.persist(user);

        PointPolicy pointPolicy = new PointPolicy("Policy1", PointPolicyType.PERCENTAGE, BigDecimal.valueOf(10.0));
        entityManager.persist(pointPolicy);

        PointHistory history1 = new PointHistory(50, PointType.EARNED, user, pointPolicy);
        pointHistoryRepository.save(history1);

        PointHistory history2 = new PointHistory(-20, PointType.USED, user, pointPolicy);
        pointHistoryRepository.save(history2);
    }

    @Test
    @DisplayName("findByUserId: Should return paginated point history for a user")
    void testFindByUserIdWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 1);

        // When
        Page<PointHistory> result = pointHistoryRepository.findByUserId(user.getId(), pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("findByUserId: Should return all point history for a user")
    void testFindByUserId() {
        // When
        List<PointHistory> result = pointHistoryRepository.findByUserId(user.getId());

        // Then
        assertThat(result)
                .isNotNull()
                .hasSize(2);
        assertThat(result.getFirst().getUser().getId()).isEqualTo(user.getId());
    }
}