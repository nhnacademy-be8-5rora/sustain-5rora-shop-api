package store.aurora.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookImage;

import java.util.List;

public interface BookImageService {
    void processApiImages(Book book, String coverUrl, List<MultipartFile> uploadedImages);

    void handleImageUpload(Book book, MultipartFile image, boolean isThumbnail);

    void handleAdditionalImages(Book book, List<MultipartFile> additionalImages);

    void deleteImages(List<Long> imageIds);

    BookImage getThumbnail(Book book);

    List<BookImage> getAdditionalImages(Book book);


}
