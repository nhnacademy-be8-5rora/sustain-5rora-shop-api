package store.aurora.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import store.aurora.user.service.impl.UserServiceImpl;

@RequiredArgsConstructor
@Service
public class UserStatusScheduler {
    private final UserServiceImpl userService;

    // 매일 자정 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkInactiveUsers() {
        userService.checkAndSetSleepStatus();
    }
}
