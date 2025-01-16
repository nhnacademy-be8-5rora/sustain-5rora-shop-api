package store.aurora.user.service.impl;

import org.springframework.stereotype.Service;
import store.aurora.user.repository.UserRankHistoryRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserRankService {

    private final UserRankHistoryRepository userRankHistoryRepository;

    public UserRankService(UserRankHistoryRepository userRankHistoryRepository) {
        this.userRankHistoryRepository = userRankHistoryRepository;
    }

    public Optional<BigDecimal> getCurrentPointRateByUserId(String userId) {
        return userRankHistoryRepository.findTopByUserIdOrderByChangedAtDesc(userId)
                .map(userRankHistory -> userRankHistory.getUserRank().getPointRate());
    }
}