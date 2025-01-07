package store.aurora.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.aurora.user.entity.Rank;
import store.aurora.user.entity.UserRank;
import store.aurora.user.entity.UserRankHistory;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRankHistoryRepository extends JpaRepository<UserRankHistory, Long> {
    // 특정 userId에 대한 등급 이력을 최신순으로 조회
    List<UserRankHistory> findByUserIdOrderByChangedAtDesc(String userId);

    // 특정 userId의 최신 등급 이력을 조회
    Optional<UserRankHistory> findLatestRankByUserId(String userId);

    // 조회: rank_name 값 가져오기
    @Query("SELECT urh.userRank.rankName FROM UserRankHistory urh " +
            "WHERE urh.user.id = :userId " +
            "ORDER BY urh.changedAt DESC")
    Optional<Rank> findLatestRankNameByUserId(String userId);

}
