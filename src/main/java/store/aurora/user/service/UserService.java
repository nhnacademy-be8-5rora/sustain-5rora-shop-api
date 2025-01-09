package store.aurora.user.service;

import store.aurora.user.dto.SignUpRequest;
import store.aurora.user.dto.UserDetailResponseDto;
import store.aurora.user.dto.UserInfoResponseDto;
import store.aurora.user.dto.UserResponseDto;
import store.aurora.user.entity.User;

public interface UserService {
    void registerUser(SignUpRequest signUpRequest);
    void registerOauthUser(SignUpRequest signUpRequest);
    void deleteUser(String userId);
    void reactivateUser(String userId);
    void checkAndSetSleepStatus();
    User getUser(String userId);
    UserDetailResponseDto getPasswordAndRole(String userId);
    UserResponseDto getUserByUserId(String userId);
    Boolean isUserExists(String userId);
    UserInfoResponseDto getUserInfo(String userId);
}
