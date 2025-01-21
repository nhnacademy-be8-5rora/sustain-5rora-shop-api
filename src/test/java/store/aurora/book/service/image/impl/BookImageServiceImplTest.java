package store.aurora.book.service.image.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookImage;
import store.aurora.book.repository.image.BookImageRepository;
import store.aurora.file.ObjectStorageService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BookImageServiceImplTest {

    @InjectMocks
    private BookImageServiceImpl bookImageService;

    @Mock
    private ObjectStorageService objectStorageService;

    @Mock
    private BookImageRepository bookImageRepository;

    @Mock
    private MultipartFile mockFile;

    private Book testBook;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testBook = new Book();

        // 공통적인 모의 객체 설정
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
    }



    @Test
    @DisplayName("handleImageUpload - 파일 업로드 성공 시 BookImage가 추가됨")
    void testHandleImageUpload_Success() {
        when(objectStorageService.uploadObject(mockFile)).thenReturn("https://storage.example.com/image.jpg");

        bookImageService.handleImageUpload(testBook, mockFile, false);

        verify(objectStorageService).uploadObject(mockFile);
    }


    @Test
    @DisplayName("deleteImages - 이미지 삭제 성공")
    void testDeleteImages_Success() {
        BookImage bookImage = new BookImage(testBook, "https://storage.example.com/image.jpg", false);
        when(bookImageRepository.findAllById(anyList())).thenReturn(List.of(bookImage));

        bookImageService.deleteImages(List.of(1L));

        verify(objectStorageService).deleteObject(bookImage.getFilePath());
        verify(bookImageRepository).delete(bookImage);
    }


    @Test
    @DisplayName("getThumbnail - 책의 썸네일 이미지를 정상적으로 가져옴")
    void testGetThumbnail() {
        BookImage thumbnail = new BookImage(testBook, "https://storage.example.com/thumbnail.jpg", true);
        testBook.addBookImage(thumbnail);

        assertThat(bookImageService.getThumbnail(testBook)).isEqualTo(thumbnail);
    }

    @Test
    @DisplayName("getAdditionalImages - 책의 추가 이미지만 가져옴")
    void testGetAdditionalImages() {
        BookImage additionalImage = new BookImage(testBook, "https://storage.example.com/additional.jpg", false);
        testBook.addBookImage(additionalImage);

        assertThat(bookImageService.getAdditionalImages(testBook)).containsExactly(additionalImage);
    }

    @Test
    @DisplayName("handleAdditionalImages - 여러 개의 추가 이미지 업로드 성공")
    void testHandleAdditionalImages_Success() {
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);

        when(file1.isEmpty()).thenReturn(false);
        when(file2.isEmpty()).thenReturn(false);
        when(objectStorageService.uploadObject(file1)).thenReturn("https://storage.example.com/image1.jpg");
        when(objectStorageService.uploadObject(file2)).thenReturn("https://storage.example.com/image2.jpg");

        bookImageService.handleAdditionalImages(testBook, List.of(file1, file2));

        verify(objectStorageService).uploadObject(file1);
        verify(objectStorageService).uploadObject(file2);
    }
}