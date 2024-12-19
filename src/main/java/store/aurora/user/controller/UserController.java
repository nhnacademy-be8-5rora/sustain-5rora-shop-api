package store.aurora.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.user.dto.SignUpRequest;
import store.aurora.user.dto.UserDetailResponseDto;
import store.aurora.user.dto.UserResponseDto;
import store.aurora.user.service.DoorayMessengerService;
import store.aurora.user.service.UserService;

import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final DoorayMessengerService doorayMessengerService;

    @GetMapping("/auth/details")
    public ResponseEntity<UserDetailResponseDto> getUserDetail(@RequestHeader("UserId") String userId) {
        UserDetailResponseDto userResponseDto = userService.getPasswordAndRole(userId);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/auth/me")
    public ResponseEntity<UserResponseDto> getUser(@RequestHeader("X-USER-ID") String userId) {
        UserResponseDto userResponseDto = userService.getUserByUserId(userId);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/auth/exists")
    public boolean checkUserExistence(@RequestHeader String userId) {
        return userService.isUserExists(userId);
    }

    // 회원가입(등록)
    @PostMapping
    public ResponseEntity<Map<String, String>> signUp(@RequestBody @Valid SignUpRequest request,
                                                      @RequestParam boolean isOauth) {
        if (isOauth) {
            userService.registerOauthUser(request);
        } else {
            userService.registerUser(request);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "회원가입이 완료되었습니다."));
    }

    // 인증코드 생성 및 전송
    @PostMapping("/send-verification-code")
    public ResponseEntity<Map<String, String>> sendCode(@RequestBody SignUpRequest request) {
        String verificationCode = String.format("%06d", new Random().nextInt(999999));  // 인증 코드 생성
        doorayMessengerService.sendVerificationCode(request.getPhoneNumber(), verificationCode);
        return ResponseEntity.ok(Map.of("message","인증 코드가 전송되었습니다."));
    }

    // 인증코드 검증
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, String>> verifyCode(@RequestBody SignUpRequest request) {
        boolean isVerified = doorayMessengerService.verifyCode(request.getPhoneNumber(), request.getVerificationCode());

        if (!isVerified) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "잘못된 인증 코드입니다. 또는 코드가 만료되었습니다."));
        } else {
            return ResponseEntity.ok(Map.of("message", "인증이 완료되었습니다."));
        }
    }

    // 회원탈퇴
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "회원탈퇴가 완료되었습니다."));
    }

    // 휴면해제처리
    @PostMapping("/reactivate")
    public ResponseEntity<Map<String, String>> reactivateUser(@RequestParam String userId) {
        userService.reactivateUser(userId);
        return ResponseEntity.ok(Map.of("message", "휴면 계정이 활성화되었습니다."));
    }

}
