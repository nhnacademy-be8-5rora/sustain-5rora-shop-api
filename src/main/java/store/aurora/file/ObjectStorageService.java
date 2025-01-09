package store.aurora.file;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    @Value("${nhncloud.storage.url}")
    private String storageUrl;

    @Value("${nhncloud.storage.container}")
    private String containerName;

    private final RestTemplate restTemplate;
    private final TokenManager tokenManager;

    public String uploadObject(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }
        // 고유한 파일 이름 생성
        String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());

        // 업로드 URL 생성
        String url = String.format("%s/%s/%s", storageUrl, containerName, uniqueFileName);
        URI uri = URI.create(url);

        // REST 요청 실행
        restTemplate.execute(uri, HttpMethod.PUT, request -> {
            request.getHeaders().add("X-Auth-Token", tokenManager.getToken());
            try (InputStream inputStream = file.getInputStream()) {
                StreamUtils.copy(inputStream, request.getBody());
            }
        }, response -> {
            if (response.getStatusCode() != HttpStatus.CREATED) {
                String error = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                throw new IOException("Failed to upload object: " + error);
            }
            return null;
        });

        // 업로드된 파일의 URL 반환
        return generatePublicUrl(uniqueFileName);
    }

    public String uploadObjectFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalArgumentException("Image URL is empty or null");
        }
        // 이미지 다운로드
        byte[] imageBytes = restTemplate.execute(URI.create(imageUrl), HttpMethod.GET, null, response -> {
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new IOException("Failed to download image from URL");
            }
            return StreamUtils.copyToByteArray(response.getBody());
        });

        // 고유한 파일 이름 생성
        String extension = FilenameUtils.getExtension(imageUrl);
        String uniqueFileName = UUID.randomUUID().toString() + (extension.isEmpty() ? ".jpg" : "." + extension);

        // 업로드 URL 생성
        String url = String.format("%s/%s/%s", storageUrl, containerName, uniqueFileName);
        URI uri = URI.create(url);

        // 업로드 요청
        restTemplate.execute(uri, HttpMethod.PUT, request -> {
            request.getHeaders().add("X-Auth-Token", tokenManager.getToken());
            StreamUtils.copy(imageBytes, request.getBody());
        }, response -> {
            if (response.getStatusCode() != HttpStatus.CREATED) {
                String error = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                throw new IOException("Failed to upload object: " + error);
            }
            return null;
        });

        // 업로드된 파일의 URL 반환
        return generatePublicUrl(uniqueFileName);

    }
    public void deleteObject(String objectUrl) {
        if (objectUrl == null || objectUrl.isEmpty()) {
            throw new IllegalArgumentException("Object URL is empty or null");
        }

        // 삭제 URL 생성
        URI uri = URI.create(objectUrl);

        // REST 요청 실행
        restTemplate.execute(uri, HttpMethod.DELETE, request -> {
            request.getHeaders().add("X-Auth-Token", tokenManager.getToken());
        }, response -> {
            if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
                String error = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                throw new IOException("Failed to delete object: " + error);
            }
            return null;
        });
    }


    public String generateUniqueFileName(String originalFilename) {
        String extension = FilenameUtils.getExtension(originalFilename);
        String baseName = UUID.randomUUID().toString();
        return baseName + "." + extension;
    }

    public String generatePublicUrl(String objectName) {
        return String.format("%s/%s/%s", storageUrl, containerName, objectName);
    }
}