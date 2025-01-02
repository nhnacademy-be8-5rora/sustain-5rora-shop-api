package store.aurora.book.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.*;
import store.aurora.book.dto.aladin.BookDetailDto;
import store.aurora.book.dto.aladin.BookRequestDto;
import store.aurora.book.dto.aladin.BookResponseDto;
import store.aurora.book.entity.Book;

import java.util.List;

public interface BookService {
    void saveDirectBook(BookRequestDto bookDto, MultipartFile coverImage, List<MultipartFile> additionalImages);
    void saveBookFromApi(BookRequestDto bookDto, List<MultipartFile> additionalImages);

    @Transactional
    void updateBook(Long bookId, BookRequestDto bookDto,
                    MultipartFile coverImage,
                    List<MultipartFile> additionalImages,
                    List<Long> deleteImageIds);

    BookRequestDto findBookRequestDtoById(String isbn13);

    Page<BookResponseDto> getAllBooks(Pageable pageable);

    @Transactional(readOnly = true)
    BookDetailDto getBookDetailsForAdmin(Long bookId);

    Book getBookById(Long bookId);
    BookDetailsDto getBookDetails(Long bookId);
    List<BookInfoDTO> getBookInfo(List<Long> bookIds);
    void notExistThrow(Long bookId);

    // aladin
    List<BookRequestDto> searchBooks(String query, String queryType, String searchTarget, int start);
}