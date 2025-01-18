package store.aurora.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
            if (StringUtils.isEmpty(response)) {
                throw new TokenRefreshException("토큰 응답이 비어 있습니다.");
            }
            // JSON 응답 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            token = root.path("access").path("token").path("id").asText();
            String expiry = root.path("access").path("token").path("expires").asText();

            if (StringUtils.isEmpty(token)) {
                throw new TokenRefreshException("토큰이 응답에서 찾을 수 없습니다.");
            }
            // 만료 시간 파싱
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            tokenExpiry = LocalDateTime.parse(expiry, formatter).minusMinutes(5); // 5분의 여유 추가

        }  catch (HttpClientErrorException e) {
            throw new TokenRefreshException("토큰 요청이 실패했습니다. HTTP 상태 코드: " + e.getStatusCode(), e);
        } catch (RestClientException e) {
            throw new TokenRefreshException("토큰 요청 중 네트워크 오류가 발생했습니다.", e);
        } catch (DateTimeParseException e) {
            throw new TokenRefreshException("토큰 만료 시간 파싱에 실패했습니다.", e);
        } catch (Exception e) {
            throw new TokenRefreshException("토큰 갱신 중 예상치 못한 오류가 발생했습니다.", e);
        }
    }
}