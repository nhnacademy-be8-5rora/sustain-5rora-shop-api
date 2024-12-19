//package store.aurora.file;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.UUID;
//
//@Service
//@Profile("dev")
//public class LocalFileStorageServiceImpl implements FileStorageService {
//
//    @Value("${storage.local.base-path}")
//    private String basePath;
//
//    @Override
//    public String uploadFile(MultipartFile file, String directory) throws IOException {
//        // 도서 디렉토리 생성: Books/{bookId}/
//        Path uploadPath = Paths.get(basePath, directory);
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        // 파일 저장
//        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
//        Path filePath = uploadPath.resolve(filename);
//        file.transferTo(filePath);
//
//        return filePath.toString(); // 파일 경로 반환
//    }
//
//    @Override
//    public void deleteFile(String filePath) throws IOException {
//        Path path = Paths.get(filePath);
//        if (Files.exists(path)) {
//            Files.delete(path);
//        } else {
//            throw new IOException("File not found: " + filePath);
//        }
//    }
//
//    @Override
//    public String getFileUrl(String filePath) {
//        return "http://localhost:8083/uploads/" + filePath; // API 경로 반환
//    }
//}