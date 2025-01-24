package store.aurora.book.service.image.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookImage;
import store.aurora.book.exception.book.BookNotFoundException;
import store.aurora.book.exception.image.ImageNotFoundException;
import store.aurora.book.repository.image.BookImageRepository;
import store.aurora.book.service.image.BookImageService;
import store.aurora.file.service.ImageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookImageServiceImpl implements BookImageService {

    private final BookImageRepository bookImageRepository;
    private final ImageService imageService;

    @Transactional
    @Override
    public void processBookImages(Book book, String coverUrl, MultipartFile coverImage, List<MultipartFile> additionalImages) {
        // 1. 알라딘 API에서 제공하는 커버 이미지 URL이 있는 경우 저장
        if (StringUtils.isNotBlank(coverUrl)) {
            String modifiedCoverUrl = modifyCoverUrl(coverUrl);
            String uploadedCoverUrl = imageService.downloadAndSaveImage(modifiedCoverUrl, book.getId(), "books");
            addBookImage(book, uploadedCoverUrl, true); // 썸네일로 저장
        }

        // 2. 사용자가 직접 업로드한 커버 이미지가 있는 경우 저장
        if (coverImage != null && !coverImage.isEmpty()) {
            handleImageUpload(book, coverImage, true);
        }

        // 3. 추가 이미지 처리
        List<MultipartFile> validImages = filterValidImages(additionalImages);
        if (!validImages.isEmpty()) {
            handleAdditionalImages(book, validImages);
        }
    }

    @Override
    public void handleImageUpload(Book book, MultipartFile file, boolean isThumbnail) {
        if (file == null || file.isEmpty()) {
            return;
        }
        String uploadedFileUrl = imageService.saveImage(file, book.getId(), "books");
        addBookImage(book, uploadedFileUrl, isThumbnail);
    }

    @Override
    public void handleAdditionalImages(Book book, List<MultipartFile> additionalImages) {
        if (CollectionUtils.isEmpty(additionalImages)) {
            return;
        }
        for (MultipartFile image : additionalImages) {
            handleImageUpload(book, image, false);
        }
    }

    @Transactional
    @Override
    public void deleteImages(List<Long> imageIds) {
        if (CollectionUtils.isEmpty(imageIds)) {
            throw new ImageNotFoundException("삭제할 이미지 목록이 비어 있습니다.");
        }

        // 데이터베이스에서 삭제할 이미지 조회
        List<BookImage> imagesToDelete = bookImageRepository.findAllById(imageIds);

        if (CollectionUtils.isEmpty(imagesToDelete)) {
            throw new ImageNotFoundException("삭제할 이미지를 찾을 수 없습니다. ID 목록: " + imageIds);
        }

        imagesToDelete.forEach(image -> {
            // 1. 오브젝트 스토리지에서 이미지 삭제
            imageService.deleteImage(image.getFilePath());

            // 2. Book 엔티티에서 이미지 제거
            Book book = image.getBook();
            if (book == null) {
                throw new BookNotFoundException("이미지에 연결된 책을 찾을 수 없습니다.");
            }
            book.getBookImages().remove(image);

            // 3. BookImage 엔티티 삭제
            bookImageRepository.delete(image);
        });
    }

    @Override
    public BookImage getThumbnail(Book book) {
        return book.getBookImages().stream()
                .filter(BookImage::isThumbnail)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<BookImage> getAdditionalImages(Book book) {
        return book.getBookImages().stream()
                .filter(image -> !image.isThumbnail())
                .toList();
    }

    private void addBookImage(Book book, String filePath, boolean isThumbnail) {
        BookImage bookImage = new BookImage(book, filePath, isThumbnail);
        book.addBookImage(bookImage);
    }

    // null 로 넘어오지 않고 빈파일을 포함해 반환하여 빈 파일 제거하는 로직
    private List<MultipartFile> filterValidImages(List<MultipartFile> images) {
        return images.stream()
                .filter(file -> !file.isEmpty()) // 빈 파일 제거
                .toList();
    }

    private String modifyCoverUrl(String coverUrl) {
        return StringUtils.replace(coverUrl, "coversum", "cover500");
    }
}