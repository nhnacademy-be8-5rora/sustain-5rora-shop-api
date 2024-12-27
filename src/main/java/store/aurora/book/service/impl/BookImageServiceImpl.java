package store.aurora.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    @Override
    public void processAndSaveImages(Book book, List<MultipartFile> uploadedImages) {
        if (uploadedImages != null && !uploadedImages.isEmpty()) {
            for (int i = 0; i < uploadedImages.size(); i++) {
                String filePath = imageService.storeImage(uploadedImages.get(i));
                boolean isThumbnail = (i == 0); // 첫 번째 이미지만 썸네일로 설정
                addBookImage(book, filePath, isThumbnail);
            }
        }
    }

    @Override
    public void processApiImages(Book book, String coverUrl, List<MultipartFile> uploadedImages) {
        String modifiedCoverUrl = modifyCoverUrl(coverUrl);
        if (modifiedCoverUrl != null) {
            // API에서 제공된 표지 이미지는 경로만 저장
            addBookImage(book, modifiedCoverUrl, true);
        }
        // 추가 이미지를 세부 이미지로 저장
        if (uploadedImages != null && !uploadedImages.isEmpty()) {
            for (MultipartFile image : uploadedImages) {
                String filePath = imageService.storeImage(image);
                addBookImage(book, filePath, false);
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
        BookImage bookImage = new BookImage();
        bookImage.setFilePath(filePath);
        bookImage.setThumbnail(isThumbnail);
        bookImage.setBook(book);
        book.addBookImage(bookImage);
    }


    private String modifyCoverUrl(String coverUrl) {
        if (coverUrl != null && coverUrl.contains("coversum")) {
            return coverUrl.replace("coversum", "cover500");
        }
        return coverUrl;
    }

}
