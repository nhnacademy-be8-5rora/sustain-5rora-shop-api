package store.aurora.point.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.point.entity.PointPolicy;
import store.aurora.point.entity.PointPolicyType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfiguration.class)
class PointPolicyRepositoryTest {

    @Autowired
    private PointPolicyRepository pointPolicyRepository;

    @Test
    @DisplayName("existsByPointPolicyName: Should return true if policy name exists in the database")
    void testExistsByPointPolicyName_Exists() {
        // Given
        PointPolicy policy = new PointPolicy(1, "Loyalty Points", PointPolicyType.PERCENTAGE, BigDecimal.valueOf(10.0));
        pointPolicyRepository.save(policy);

        // When
        boolean exists = pointPolicyRepository.existsByPointPolicyName("Loyalty Points");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByPointPolicyName: Should return false if policy name does not exist in the database")
    void testExistsByPointPolicyName_NotExists() {
        // When
        boolean exists = pointPolicyRepository.existsByPointPolicyName("Nonexistent Policy");

        // Then
        assertThat(exists).isFalse();
    }
}