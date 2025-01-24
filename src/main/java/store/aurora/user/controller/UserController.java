package store.aurora.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.common.dto.ErrorResponseDto;
import store.aurora.point.entity.PointPolicyCategory;
import store.aurora.point.service.PointHistoryService;
import store.aurora.user.dto.*;
import store.aurora.user.entity.User;
import store.aurora.user.service.DoorayMessengerService;
import store.aurora.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final DoorayMessengerService doorayMessengerService;
    private final PointHistoryService pointHistoryService;

    private static final Logger LOG = LoggerFactory.getLogger("user-logger");

    @GetMapping("/auth/details")
    public ResponseEntity<UserDetailResponseDto> getUserDetail(@RequestHeader("UserId") String userId) {
        UserDetailResponseDto userResponseDto = userService.getPasswordAndRole(userId);
        return ResponseEntity.ok(userResponseDto);
        // todo: 회원탈퇴 후 다시 로그인할 시 상태코드 404 전송
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

    // 회원정보 조회
    @GetMapping("/info")
    @Operation(summary = "회원정보 조회", description = "사용자의 회원정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    public ResponseEntity<UserInfoResponseDto> getUserInfo(@RequestHeader("userId") String userId) {
        UserInfoResponseDto userResponseDto = userService.getUserInfo(userId);
        return ResponseEntity.ok(userResponseDto);
    }

    // 회원가입(등록)
    @PostMapping
    @Operation(summary = "회원가입", description = "회원가입을 처리합니다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"message\":\"회원가입이 완료되었습니다.\"}")))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    public ResponseEntity<Map<String, String>> signUp(@RequestBody @Valid SignUpRequest request,
                                                      @RequestParam boolean isOauth) {
        User savedUser = isOauth ? userService.registerOauthUser(request) : userService.registerUser(request);

        try {
            pointHistoryService.earnPoint(PointPolicyCategory.SIGNUP, savedUser);
        } catch (Exception e) {
            LOG.error("Failed to earn points: category=signup, userId={}", savedUser.getId(), e);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "회원가입이 완료되었습니다."));
    }

    // 인증코드 생성 및 전송
    @Operation(summary = "인증코드 생성 및 전송", description = "입력된 전화번호로 인증코드를 생성하여 전송합니다.")
    @ApiResponse(responseCode = "200", description = "인증코드 전송 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"message\":\"인증 코드가 전송되었습니다.\"}")))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @PostMapping("/send-verification-code")
    public ResponseEntity<Map<String, String>> sendCode(@RequestParam String phoneNumber) {
        String verificationCode = String.format("%06d", new Random().nextInt(999999));  // 인증 코드 생성
        doorayMessengerService.sendVerificationCode(phoneNumber, verificationCode);
        return ResponseEntity.ok(Map.of("message","인증 코드가 전송되었습니다."));
    }

    // 인증코드 검증
    @Operation(summary = "인증코드 검증", description = "입력된 인증코드와 전화번호로 인증을 검증합니다.")
    @ApiResponse(responseCode = "200", description = "인증 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"message\":\"인증이 완료되었습니다.\"}")))
    @ApiResponse(responseCode = "400", description = "인증 실패 - 잘못된 인증 코드 또는 코드 만료",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"message\":\"잘못된 인증 코드입니다. 또는 코드가 만료되었습니다.\"}")))
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, String>> verifyCode(@RequestBody VerificationRequest request) {
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
    @Operation(summary = "회원탈퇴", description = "사용자의 계정을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "회원탈퇴 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "회원탈퇴가 완료되었습니다."));
    }

    // 휴면해제처리
    @Operation(summary = "휴면 계정 활성화", description = "입력된 사용자 ID를 기반으로 휴면 계정을 활성화합니다.")
    @ApiResponse(responseCode = "200", description = "계정 활성화 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"message\":\"휴면 계정이 활성화되었습니다.\"}")))
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @PostMapping("/reactivate")
    public ResponseEntity<Map<String, String>> reactivateUser(@RequestParam String userId) {
        userService.reactivateUser(userId);
        return ResponseEntity.ok(Map.of("message", "휴면 계정이 활성화되었습니다."));
    }

    @GetMapping("birth/coupon")
    List<String> getUserIdByMonth(@RequestParam int currentMonth){
        return userService.searchByMonth(currentMonth);
    }

    @Operation(summary = "로그인 날짜 변경", description = "제공된 userId에 해당하는 유저의 마지막 로그인 날짜 업데이트")
        @ApiResponse(responseCode = "204", description = "로그인 날짜 업데이트 성공")
        @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없음",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDto.class)))
    @PatchMapping("/{userId}/last-login")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLastLogin(@PathVariable String userId,
                                @RequestBody LocalDateTime lastLogin) {
        userService.updateLastLogin(userId, lastLogin);
    }

    // 회원정보 수정
    @Operation(summary = "회원정보 수정", description = "입력된 사용자 ID와 요청 데이터를 기반으로 회원정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "회원정보 수정 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"message\":\"회원정보가 수정되었습니다.\"}")))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable String userId,
                                        @RequestBody UserUpdateRequestDto request) {
        userService.updateUser(userId, request);
        return ResponseEntity.ok(Map.of("message", "회원정보가 수정되었습니다."));
    }
}