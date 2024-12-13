package store.aurora.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.user.dto.SignUpRequest;
import store.aurora.user.service.UserService;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private final UserService userService;

    // 회원가입
    @PostMapping
    public ResponseEntity<Map<String, String>> signUp(@RequestBody @Valid SignUpRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다."));
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


    // 로그아웃
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(@RequestHeader(value = "") String token) {
//
//    }

}
