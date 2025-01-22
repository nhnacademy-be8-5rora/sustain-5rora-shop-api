package store.aurora.file.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.file.exception.DirectoryCreationException;
import store.aurora.file.exception.ImageDeleteException;
import store.aurora.file.exception.ImageDownloadException;
import store.aurora.file.exception.ImageSaveException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Getter
@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${file.storage.root}") // 저장 경로 지정
    private String storageRoot;

    /**
     * 사용자가 업로드한 이미지 저장
     */
    public String saveImage(MultipartFile file, Long id, String type) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        Path directoryPath = getStoragePath(id, type);
        createDirectoryIfNotExists(directoryPath);

        String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());
        Path filePath = directoryPath.resolve(uniqueFileName);

        try {
            file.transferTo(filePath);
            return String.format("/images/%s/%d/%s", type, id, uniqueFileName); // 상대 경로 반환
        } catch (IOException e) {
            throw new ImageSaveException("파일 저장 실패: " + e.getMessage());
        }
    }

    /**
     * 알라딘 API에서 제공하는 URL 기반 이미지 다운로드 후 저장
     */
    public String downloadAndSaveImage(String imageUrl, Long id, String type) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        Path directoryPath = getStoragePath(id, type);
        createDirectoryIfNotExists(directoryPath);

        String uniqueFileName = generateUniqueFileName(imageUrl);
        Path filePath = directoryPath.resolve(uniqueFileName);

        try {
            downloadFile(imageUrl, filePath);
            return String.format("/images/%s/%d/%s", type, id, uniqueFileName);
        } catch (Exception e) {
            throw new ImageDownloadException("이미지 다운로드 및 저장 실패: " + e.getMessage());
        }
    }

    public void deleteImage(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) {
            throw new IllegalArgumentException("삭제할 파일 경로가 유효하지 않습니다.");
        }

        Path filePath = Paths.get(storageRoot, storedPath);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new ImageDeleteException("파일 삭제 실패: " + storedPath);
        }
    }

    private void downloadFile(String imageUrl, Path filePath) {
        try {
            // URL 인코딩된 문자열을 디코딩하여 URI 객체 생성
            URI uri = new URI(URLDecoder.decode(imageUrl, StandardCharsets.UTF_8));

            // InputStream을 열어 URL 리소스 다운로드
            try (InputStream in = uri.toURL().openStream()) {
                Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (URISyntaxException | IOException e) {
            throw new ImageDownloadException("파일 다운로드 실패: " + e.getMessage());
        }
    }

    private String getFileExtension(String filenameOrUrl) {
        if (filenameOrUrl == null || filenameOrUrl.isBlank()) {
            return ".jpg"; // 기본 확장자 설정
        }

        // URL인지 확인 후 확장자 추출
        try {
            URI uri = new URI(filenameOrUrl);
            String path = uri.getPath(); // URL 경로 부분 추출

            int lastDotIndex = path.lastIndexOf('.');
            if (lastDotIndex > 0) {
                return path.substring(lastDotIndex); // ".jpg", ".png" 등 반환
            }
        } catch (URISyntaxException e) {
            // URL이 아니거나 파싱 실패 시 처리
        }

        String extractedExtension = FilenameUtils.getExtension(filenameOrUrl);
        return extractedExtension.isEmpty() ? ".jpg" : "." + extractedExtension;
    }

    private Path getStoragePath(Long id, String type) {
        return Paths.get(storageRoot, type, id.toString());
    }
    private String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return String.format("%s_%s%s", UUID.randomUUID(), "image", extension);
    }

    private void createDirectoryIfNotExists(Path directoryPath) {
        try {
            Files.createDirectories(directoryPath);
        } catch (IOException e) {
            throw new DirectoryCreationException("디렉토리 생성 실패: " + directoryPath);
        }
    }
}