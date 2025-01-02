package store.aurora.point.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.aurora.point.entity.PointHistory;

import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    Page<PointHistory> findByUserId(String userId, Pageable pageable);
    List<PointHistory> findByUserId(String userId);
}
