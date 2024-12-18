package store.aurora.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // 프로젝트 실행 디렉토리 기준으로 폴더 생성 후 저장
//        Path uploadPath = Paths.get(System.getProperty("user.dir"), uploadDir);

        // 홈 디렉토리에 폴더 생성 후 저장
        Path uploadPath = Paths.get(uploadDir);
        File directory = uploadPath.toFile();

        // 디렉토리 자동 생성
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 파일 저장
        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }
}