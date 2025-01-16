package store.aurora.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import store.aurora.user.service.UserService;

@RequiredArgsConstructor
@Component
public class UserSleepStatusScheduler {
    private final UserService userService;

    @Scheduled(cron = "0 10 16 * * * ")
    public void checkAndSetSleepStatus() {
        userService.checkAndSetSleepStatus();
    }

}
