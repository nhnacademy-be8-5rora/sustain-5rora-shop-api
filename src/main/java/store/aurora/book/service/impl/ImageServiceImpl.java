package store.aurora.book.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.service.ImageService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private static final String UPLOAD_DIR = "/Users/ksc4305/nhn/5rora/apiEx/upload/";

    @PostConstruct
    @Override
    public void init() {
        // 업로드 디렉토리 생성
        File uploadDirectory = new File(UPLOAD_DIR);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }
    }
    @Override
    public String storeImage(MultipartFile file) {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path targetPath = Paths.get(UPLOAD_DIR).resolve(fileName);

        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }

        return "/uploaded-files/" + fileName;
    }

    @Override
    public String getDefaultCoverImagePath() {
        return "/uploaded-files/default-cover.jpg";
    }

}
