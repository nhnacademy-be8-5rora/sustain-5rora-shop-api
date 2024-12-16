package store.aurora.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import store.aurora.user.client.MessageSendClient;
import store.aurora.user.dto.MessagePayload;
import store.aurora.user.service.DoorayMessengerService;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class DoorayMessengerServiceImpl implements DoorayMessengerService {
    private final MessageSendClient messageSendClient;
    private final RedisTemplate<String, Object> redisTemplate;

    private long verificationCodeExpiration = 300;  // 인증 코드 만료 시간 (초 단위)

    // 인증 코드 전송
    @Override
    public void sendVerificationCode(String phoneNumber, String verificationCode) {
        // Redis에 인증코드 저장
        redisTemplate.opsForValue().set(phoneNumber, verificationCode, Duration.ofSeconds(verificationCodeExpiration));

        // 메세지 전송
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setBotName("5rora");
        messagePayload.setText(verificationCode);

        Long serviceId = 3204376758577275363L;
        Long botId = 3942252259979529648L;
        String botToken = "QOdX3Qt9TuODhwaSkULPeA";
        messageSendClient.sendMessage(messagePayload, serviceId, botId, botToken);
    }

    // 인증 코드 검증
    @Override
    public boolean verifyCode(String phoneNumber, String inputCode) {
        String storedCode = (String) redisTemplate.opsForValue().get(phoneNumber);

        if (storedCode == null) {
            return false;
        }
        if (storedCode.equals(inputCode)) {
            // 인증 성공하면 인증 상태를 Redis에 저장
            redisTemplate.opsForValue().set(phoneNumber + "_verified", "true", Duration.ofMinutes(10));
            redisTemplate.delete(phoneNumber);  // 인증코드 삭제
            return true;
        }

        return false;
    }
}
