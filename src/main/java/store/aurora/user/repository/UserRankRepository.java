package store.aurora.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.aurora.user.entity.UserRank;

@Repository
public interface UserRankRepository extends JpaRepository<UserRank, Long> {

    UserRank findByRankName(String rankName);
}
