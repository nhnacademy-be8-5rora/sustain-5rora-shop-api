package store.aurora.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookImage;
import store.aurora.book.service.BookImageService;
import store.aurora.book.service.ImageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookImageServiceImpl implements BookImageService {

    private final ImageService imageService;
    @Transactional
    @Override
    public void processApiImages(Book book, String coverUrl, List<MultipartFile> uploadedImages) {
        if (coverUrl != null) {
            String modifiedCoverUrl = modifyCoverUrl(coverUrl);
            if (modifiedCoverUrl != null) {
                addBookImage(book, modifiedCoverUrl, true); // 썸네일로 저장
            }
        }else {
            throw new IllegalArgumentException("Modified coverUrl is null");
        }
        handleAdditionalImages(book, uploadedImages);
    }

    @Override
    public void handleImageUpload(Book book, MultipartFile image, boolean isThumbnail) {
        if (image != null && !image.isEmpty()) {
            String filePath = imageService.storeImage(image);
            addBookImage(book, filePath, isThumbnail);
        }
    }

    @Override
    public void handleAdditionalImages(Book book, List<MultipartFile> additionalImages) {
        if (additionalImages != null && !additionalImages.isEmpty()) {
            for (MultipartFile image : additionalImages) {
                handleImageUpload(book, image, false);
            }
        }
    }

    @Override
    public String getThumbnailPath(Book book) {
        if (book == null || book.getBookImages() == null) {
            return imageService.getDefaultCoverImagePath(); // 명시적 경로 반환
        }
        return book.getBookImages().stream()
                .filter(BookImage::isThumbnail)
                .map(BookImage::getFilePath)
                .findFirst()
                .orElse(imageService.getDefaultCoverImagePath());
    }

    private void addBookImage(Book book, String filePath, boolean isThumbnail) {
        BookImage bookImage = new BookImage(book,filePath,isThumbnail);
        book.addBookImage(bookImage);
    }

    private String modifyCoverUrl(String coverUrl) {
        if (coverUrl != null && coverUrl.contains("coversum")) {
            return coverUrl.replace("coversum", "cover500");
        }
        return coverUrl;
    }

}
