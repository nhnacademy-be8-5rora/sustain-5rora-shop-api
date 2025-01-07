package store.aurora.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.point.entity.PointPolicy;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Integer> {
    boolean existsByPointPolicyName(String pointPolicyName);
}