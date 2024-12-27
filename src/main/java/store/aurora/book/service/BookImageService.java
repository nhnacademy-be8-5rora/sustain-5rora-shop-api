package store.aurora.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.entity.Book;

import java.util.List;

public interface BookImageService {
    void processAndSaveImages(Book book, List<MultipartFile> uploadedImages);
    void processApiImages(Book book, String coverUrl, List<MultipartFile> uploadedImages);

    String getThumbnailPath(Book book);
}
