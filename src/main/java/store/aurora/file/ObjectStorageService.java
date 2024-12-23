package store.aurora.file;

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
public class ObjectStorageService {

    @Value("${nhncloud.storage.url}")
    private String storageUrl;

    @Value("${nhncloud.storage.token}")
    private String tokenId;

    private final RestTemplate restTemplate;

    public ObjectStorageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String uploadObject(String containerName, String objectName, MultipartFile file) throws IOException {
        String url = String.format("%s/%s/%s", storageUrl, containerName, objectName);
        URI uri = URI.create(url);

        restTemplate.execute(uri, HttpMethod.PUT, request -> {
            request.getHeaders().add("X-Auth-Token", tokenId);
            try (InputStream inputStream = file.getInputStream()) {
                StreamUtils.copy(inputStream, request.getBody());
            }
        }, response -> {
            if (response.getStatusCode() != HttpStatus.CREATED) {
                String error = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                throw new IOException("Failed to upload object: " + error); // response.getStatusCode()
            }
            return null;
        });

        return url;
    }

    public String generateUniqueFileName(String originalFilename) {
        String extension = FilenameUtils.getExtension(originalFilename);
        String baseName = UUID.randomUUID().toString();
        return baseName + "." + extension;
    }
}