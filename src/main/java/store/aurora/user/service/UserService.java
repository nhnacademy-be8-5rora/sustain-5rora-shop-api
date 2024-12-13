package store.aurora.user.service;

import store.aurora.user.dto.SignUpRequest;

public interface UserService {
    void registerUser(SignUpRequest signUpRequest);
    void deleteUser(String userId);
    void reactivateUser(String userId);
    void checkAndSetSleepStatus();
}
