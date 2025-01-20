package store.aurora.book.service.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.*;
import store.aurora.book.dto.aladin.*;
import store.aurora.book.entity.Book;
import store.aurora.search.dto.BookSearchResponseDTO;

import java.util.List;
import java.util.Optional;

public interface BookService {

    void saveBook(BookRequestDto bookDto, MultipartFile coverImage, List<MultipartFile> additionalImages);

    void saveBookFromApi(AladinBookRequestDto bookDto, List<MultipartFile> additionalImages);

    void updateBook(Long bookId, BookRequestDto bookDto,
                    MultipartFile coverImage,
                    List<MultipartFile> additionalImages,
                    List<Long> deleteImageIds);

    Page<BookResponseDto> getBooksByActive(boolean isActive, Pageable pageable);

    void updateBookActivation(Long bookId, boolean isActive);

    void updateBookStockOnOrder(Long bookId, int quantity);

    BookDetailDto getBookDetailsForAdmin(Long bookId);

    Book getBookById(Long bookId);

    BookDetailsDto getBookDetails(Long bookId);

    List<BookInfoDTO> getBookInfo(List<Long> bookIds);

    void notExistThrow(Long bookId);

    Page<BookSearchResponseDTO> getBooksByLike(String userId, Pageable pageable);

    Optional<BookSearchResponseDTO> findMostSeller();


}