package store.aurora.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import store.aurora.user.service.UserService;
import store.aurora.user.service.impl.UserRankService;

@RequiredArgsConstructor
@Component
public class UserScheduler {
    private final UserService userService;
    private final UserRankService userRankService;

    // 휴면상태로 변경
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkAndSetSleepStatus() {
        userService.checkAndSetSleepStatus();
    }

    // 회원 등급 업데이트
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateUserRanks() {
        userRankService.updateUserRanks();
    }
}
