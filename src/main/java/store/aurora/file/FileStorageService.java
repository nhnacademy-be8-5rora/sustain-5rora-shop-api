package store.aurora.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    String uploadFile(MultipartFile file, String directory) throws IOException;
    void deleteFile(String filePath) throws IOException;
    String getFileUrl(String filePath);

}
