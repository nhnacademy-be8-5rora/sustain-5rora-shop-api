package store.aurora.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.user.dto.SignUpRequest;
import store.aurora.user.dto.UserDetailResponseDto;
import store.aurora.user.dto.UserResponseDto;
import store.aurora.user.entity.User;
import store.aurora.user.entity.UserRank;
import store.aurora.user.entity.UserRankHistory;
import store.aurora.user.entity.UserStatus;
import store.aurora.user.exception.DuplicateUserException;
import store.aurora.user.exception.RoleNotFoundException;
import store.aurora.user.exception.VerificationException;
import store.aurora.user.repository.UserRankHistoryRepository;
import store.aurora.user.repository.UserRankRepository;
import store.aurora.user.repository.UserRepository;
import store.aurora.user.service.DoorayMessengerService;
import store.aurora.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRankRepository userRankRepository;
    private final UserRankHistoryRepository userRankHistoryRepository;
    private final DoorayMessengerService doorayMessengerService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final int INACTIVE_PERIOD_MONTHS = 3;    // 휴면 3개월 기준
    private final RedisTemplate redisTemplate;

    // 회원가입
    @Override
    public void registerUser(SignUpRequest request) {
        // 인증 상태 확인
        String verificationStatus = (String) redisTemplate.opsForValue().get(request.getPhoneNumber() + "_verified");
        if (verificationStatus == null || !verificationStatus.equals("true")) {
            throw new VerificationException("인증 코드가 확인되지 않았습니다. 인증을 완료한 후 회원가입을 진행해주세요.");
        }

        // 유효성 검사
        if (userRepository.existsById(request.getId())) {
            throw new DuplicateUserException("이미 존재하는 아이디입니다.");
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateUserException("이미 존재하는 전화번호입니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());


        // User에 저장
        User user = new User();
        user.setId(request.getId());
        user.setPassword(encodedPassword);  // 암호화된 비밀번호 저장
        user.setName(request.getName());
        user.setBirth(LocalDate.parse(request.getBirth(), DateTimeFormatter.ofPattern("yyyyMMdd")));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setStatus(UserStatus.ACTIVE);
        user.setLastLogin(null);
        user.setSignUpDate(LocalDate.now());
        user.setIsOauth(false);

        userRepository.save(user);

        // 회원등급 저장
        UserRank userRank = userRankRepository.findByRankName("GENERAL");
        UserRankHistory userRankHistory = new UserRankHistory();
        userRankHistory.setUserRank(userRank);
        userRankHistory.setChangeReason("회원가입");
        userRankHistory.setChangedAt(LocalDateTime.now());
        userRankHistory.setUser(user);

        userRankHistoryRepository.save(userRankHistory);

        // 인증 상태 삭제
        redisTemplate.delete(request.getPhoneNumber() + "_verified");
    }

    // 회원탈퇴
    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    // 3개월 이상 로그인하지 않은 회원 휴면 처리
    @Override
    public void checkAndSetSleepStatus() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thresholdDate = now.minusMonths(INACTIVE_PERIOD_MONTHS);

        // 탈퇴회원은 제외
        List<User> inactiveUsers = userRepository.findByLastLoginBeforeAndStatusNot(thresholdDate, UserStatus.DELETED);

        for (User user: inactiveUsers) {
            user.setStatus(UserStatus.INACTIVE);
            userRepository.save(user);
        }
    }

    @Override
    public User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // 휴면 해제 처리
    @Override
    public void reactivateUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        // 인증 api에서 비밀번호 확인 인증 처리

        if (user.getStatus() == UserStatus.INACTIVE) {
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("이미 휴면해제된 계정입니다.");
        }
    }

    private String getRole(User user, String userId) {
        return user.getUserRoles().stream()
                .findFirst()
                .map(userRole -> userRole.getRole().getRoleName())
//                .orElse("ROLE_USER"); // 기본값 설정
                .orElseThrow(() -> new RoleNotFoundException(userId));
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByUserId(String userId) {
        User user = getUser(userId);
        String role = getRole(user, userId);
        return new UserResponseDto(user.getId(), role);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isUserExists(String userId) {
        return userRepository.existsById(userId);
    }

    @Transactional(readOnly = true)
    public UserDetailResponseDto getPasswordAndRole(String userId) {
        User user = getUser(userId);
        String role = getRole(user, userId);
        return new UserDetailResponseDto(user.getPassword(), role);
    }
}
