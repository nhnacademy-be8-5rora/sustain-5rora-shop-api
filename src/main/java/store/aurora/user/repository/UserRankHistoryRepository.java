package store.aurora.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.aurora.user.entity.UserRankHistory;

@Repository
public interface UserRankHistoryRepository extends JpaRepository<UserRankHistory, Long> {
}
