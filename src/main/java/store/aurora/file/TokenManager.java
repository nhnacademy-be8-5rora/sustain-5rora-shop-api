package store.aurora.file;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import store.aurora.key.KeyConfig;
import store.aurora.key.KeyManagerException;
import store.aurora.key.KeyManagerJsonParsingException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
@RequiredArgsConstructor
public class TokenManager {

    private String token;
    private LocalDateTime tokenExpiry;
    private final RestTemplate restTemplate;
    private final KeyConfig keyConfig;

    @Value("${nhncloud.identity.token-url}")
    private String tokenUrl;

    @Value("${nhncloud.identity.secret-id}")
    private String secretId;

    private String tenantId;
    private String username;
    private String password;

    public synchronized String getToken() {
        if (token == null || tokenExpiry == null || tokenExpiry.isBefore(LocalDateTime.now())) {
            loadCredentials();
            refreshToken();
        }
        return token;
    }
    private void loadCredentials() {
        try {
            // NHN Key Manager에서 한 번에 저장된 인증 정보(JSON) 가져오기
            String secretJson = keyConfig.keyStore(secretId);
            // JSON 데이터가 비어 있는 경우 예외 처리
            JsonNode authInfo = getAuthInfo(secretJson);
            tenantId = authInfo.path("tenantId").asText();
            username = authInfo.path("username").asText();
            password = authInfo.path("password").asText();
        } catch (JsonProcessingException e) {
            throw new KeyManagerJsonParsingException("Key Manager에서 인증 정보를 JSON으로 변환하는 데 실패했습니다.", e);
        }
    }

    private static JsonNode getAuthInfo(String secretJson) throws JsonProcessingException {
        if (StringUtils.isEmpty(secretJson)) {
            throw new KeyManagerException("Key Manager에서 빈 JSON 응답을 받았습니다.");
        }
        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode authInfo = objectMapper.readTree(secretJson); // 예외 발생 가능
        // 필수 필드가 누락된 경우 검증
        if (!authInfo.hasNonNull("tenantId") || !authInfo.hasNonNull("username") || !authInfo.hasNonNull("password")) {
            throw new KeyManagerException("Key Manager에서 인증 정보가 누락되었습니다: " + secretJson);
        }
        return authInfo;
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
            if (e.getStatusCode().is4xxClientError()) {
                // 인증 정보가 만료되었을 가능성이 있으므로 다시 불러오기 시도
                loadCredentials();
                refreshToken();
            }
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