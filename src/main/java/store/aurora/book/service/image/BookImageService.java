package store.aurora.book.service.image;

import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookImage;

import java.util.List;

public interface BookImageService {
    void processBookImages(Book book, String coverUrl, MultipartFile coverImage, List<MultipartFile> additionalImages);

    void handleImageUpload(Book book, MultipartFile image, boolean isThumbnail);

    void handleAdditionalImages(Book book, List<MultipartFile> additionalImages);

    void deleteImages(List<Long> imageIds);

    BookImage getThumbnail(Book book);

    List<BookImage> getAdditionalImages(Book book);


}
