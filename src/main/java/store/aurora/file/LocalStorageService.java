package store.aurora.file;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalStorageService {

    @Value("${file.storage.root:/home/5rora-images}") // 기본 저장 경로 설정
    private String storageRoot;

    public String uploadFile(MultipartFile file, Long bookId, String type) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("파일 업로드 실패: 업로드할 파일이 비어 있거나 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 저장할 디렉토리 설정
        String subfolder = switch (type) {
            case "thumbnail" -> "thumbnails";
            case "additional" -> "additional";
            case "review" -> "reviews";
            default -> throw new IllegalArgumentException("지원하지 않는 이미지 타입: " + type);
        };

        Path directoryPath = Paths.get(storageRoot, "books", bookId.toString(), subfolder);
        createDirectoryIfNotExists(directoryPath);

        // 고유한 파일명 생성
        String uniqueFileName = String.format("%s_%s.%s", type, UUID.randomUUID(), getFileExtension(file.getOriginalFilename()));
        Path filePath = directoryPath.resolve(uniqueFileName);

        try {
            file.transferTo(filePath);
        } catch (IOException e) {
            throw new FileStorageException("파일 저장 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 상대 경로 반환하여 DB에 저장 (예: "books/1/thumbnails/thumbnail_abc123.jpg")
        return String.format("books/%d/%s/%s", bookId, subfolder, uniqueFileName);
    }

    public String uploadFileFromUrl(String fileUrl, Long bookId, String type) {
        try (InputStream in = new URL(fileUrl).openStream()) {
            Path directoryPath = Paths.get(storageRoot, "books", bookId.toString(), type);
            createDirectoryIfNotExists(directoryPath);

            // 고유한 파일명 생성
            String uniqueFileName = String.format("%s_%s.jpg", type, UUID.randomUUID());
            Path filePath = directoryPath.resolve(uniqueFileName);

            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toString();
        } catch (IOException e) {
            throw new FileStorageException("파일 저장 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            throw new FileStorageException("파일 삭제 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void createDirectoryIfNotExists(Path directoryPath) {
        File directory = directoryPath.toFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private String getFileExtension(String fileName) {
        return FilenameUtils.getExtension(fileName);
    }
}