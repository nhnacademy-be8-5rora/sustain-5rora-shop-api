package store.aurora.book.service.image.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookImage;
import store.aurora.book.exception.image.ImageProcessingException;
import store.aurora.book.exception.image.ImageUploadException;
import store.aurora.book.exception.image.ImageNotFoundException;
import store.aurora.book.repository.image.BookImageRepository;
import store.aurora.book.service.image.BookImageService;
import store.aurora.file.ObjectStorageException;
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
            if (modifiedCoverUrl == null) {
                throw new ImageProcessingException("유효하지 않은 coverUrl: " + coverUrl);
            }
            try {
                String uploadedCoverUrl = objectStorageService.uploadObjectFromUrl(modifiedCoverUrl);
                if (uploadedCoverUrl == null || uploadedCoverUrl.isEmpty()) {
                    throw new ImageProcessingException("coverUrl 업로드 실패: " + modifiedCoverUrl);
                }
                addBookImage(book, uploadedCoverUrl, true); // 썸네일로 저장
            } catch (ObjectStorageException e) {
                throw new ImageProcessingException("오브젝트 스토리지 업로드 실패: " + e.getMessage());
            }
        }
        handleAdditionalImages(book, uploadedImages);
    }

    @Override
    public void handleImageUpload(Book book, MultipartFile file, boolean isThumbnail) {
        if (file == null || file.isEmpty()) {
            throw new ImageUploadException("업로드할 파일이 비어 있습니다.");
        }
        try {
            String uploadedFileUrl = objectStorageService.uploadObject(file);
            if (uploadedFileUrl == null || uploadedFileUrl.isEmpty()) {
                throw new ImageUploadException("파일 업로드 실패");
            }
            addBookImage(book, uploadedFileUrl, isThumbnail);
        } catch (ObjectStorageException e) {
            throw new ImageUploadException("오브젝트 스토리지 업로드 실패: " + e.getMessage());
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

        // 데이터베이스에서 삭제할 이미지 조회
        List<BookImage> imagesToDelete = bookImageRepository.findAllById(imageIds);

        if (imagesToDelete.isEmpty()) {
            throw new ImageNotFoundException("삭제할 이미지를 찾을 수 없습니다. ID 목록: " + imageIds);
        }

        imagesToDelete.forEach(image -> {
            try {
                // 1. 오브젝트 스토리지에서 이미지 삭제
                objectStorageService.deleteObject(image.getFilePath());
            } catch (ObjectStorageException e) {
                throw new ObjectStorageException("이미지 삭제 실패: " + image.getFilePath(), e.getStatus());
            }

            // 2. Book 엔티티에서 이미지 제거
            Book book = image.getBook();
            if (book != null) {
                book.getBookImages().remove(image);
            }

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

    private String modifyCoverUrl(String coverUrl) {
        if (coverUrl != null && coverUrl.contains("coversum")) {
            return coverUrl.replace("coversum", "cover500");
        }
        return coverUrl;
    }
}