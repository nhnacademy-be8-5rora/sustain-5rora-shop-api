package store.aurora.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookImage;
import store.aurora.book.repository.BookImageRepository;
import store.aurora.book.service.BookImageService;
import store.aurora.file.ObjectStorageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookImageServiceImpl implements BookImageService {

    private final ObjectStorageService objectStorageService;
    private final BookImageRepository bookImageRepository;
    @Override
    @Transactional
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
    public void handleImageUpload(Book book, MultipartFile file, boolean isThumbnail) {
        if (file != null && !file.isEmpty()) {
            String uploadedFileUrl = objectStorageService.uploadObject(file);
            addBookImage(book, uploadedFileUrl, isThumbnail);
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

    @Transactional
    @Override
    public void deleteImages(List<Long> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) {
            return; // 삭제할 이미지가 없는 경우 바로 반환
        }
        // 이미지 ID로 데이터베이스에서 이미지 조회
        List<BookImage> imagesToDelete = bookImageRepository.findAllById(imageIds);

        // 각 이미지 삭제
        for (BookImage image : imagesToDelete) {
            // 1. 오브젝트 스토리지에서 이미지 삭제
            objectStorageService.deleteObject(image.getFilePath());

            // 2. Book 엔티티에서 이미지를 제거
            Book book = image.getBook();
            if (book != null) {
                book.getBookImages().remove(image); // 컬렉션에서 제거
            }
        }
    }

    @Override
    public String getThumbnailPath(Book book) {
        if (book == null || book.getBookImages() == null) {
            return null;
        }
        return book.getBookImages().stream()
                .filter(BookImage::isThumbnail)
                .map(BookImage::getFilePath)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<String> getAdditionalImages(Book book) {
        if (book == null || book.getBookImages() == null) {
            return List.of();
        }
        return book.getBookImages().stream()
                .filter(image -> !image.isThumbnail()) // 썸네일이 아닌 이미지만 가져오기
                .map(BookImage::getFilePath)
                .toList();
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
