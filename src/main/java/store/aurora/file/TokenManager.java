package store.aurora.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class TokenManager {

    private String token;
    private LocalDateTime tokenExpiry;
    private final RestTemplate restTemplate;

    @Value("${nhncloud.identity.token-url}")
    private String tokenUrl;

    @Value("${nhncloud.identity.tenant-id}")
    private String tenantId;

    @Value("${nhncloud.identity.username}")
    private String username;

    @Value("${nhncloud.identity.password}")
    private String password;

    public synchronized String getToken() {
        if (token == null || tokenExpiry.isBefore(LocalDateTime.now())) {
            refreshToken();
        }
        return token;
    }
    private void refreshToken() {
        try {
            String payload = String.format(
                    "{ \"auth\": { \"tenantId\": \"%s\", \"passwordCredentials\": { \"username\": \"%s\", \"password\": \"%s\" } } }",
                    tenantId, username, password
            );

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // HTTP 엔티티 생성
            HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

            String response = restTemplate.postForObject(
                    tokenUrl,
                    requestEntity,
                    String.class
            );

            // JSON 응답 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            token = root.path("access").path("token").path("id").asText();
            String expiry = root.path("access").path("token").path("expires").asText();

            // 만료 시간 파싱
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            tokenExpiry = LocalDateTime.parse(expiry, formatter).minusMinutes(5); // 5분의 여유 추가

        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh token", e);
        }
    }
}