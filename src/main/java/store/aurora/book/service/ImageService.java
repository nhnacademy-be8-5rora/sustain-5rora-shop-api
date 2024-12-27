package store.aurora.book.service;

import jakarta.annotation.PostConstruct;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    @PostConstruct
    void init();

    String storeImage(MultipartFile file);

    String getDefaultCoverImagePath();
}
