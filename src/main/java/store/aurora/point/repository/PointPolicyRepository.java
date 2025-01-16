package store.aurora.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.point.entity.PointPolicy;
import store.aurora.point.entity.PointPolicyCategory;

import java.util.List;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Integer> {
    boolean existsByPointPolicyName(String pointPolicyName);
    List<PointPolicy> findByPointPolicyCategoryAndIsActive(PointPolicyCategory category, Boolean isActive);
}