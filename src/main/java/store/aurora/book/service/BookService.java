package store.aurora.book.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.*;
import store.aurora.book.dto.aladin.BookDto;
import store.aurora.book.entity.Book;

import java.util.List;

public interface BookService {
    void saveDirectBook(BookDto bookDto, MultipartFile coverImage, List<MultipartFile> additionalImages);
    void saveBookFromApi(BookDto bookDto, List<MultipartFile> additionalImages);

    BookDto findBookDtoById(String isbn13);

    void saveBook(BookRequestDTO requestDTO);
    void updateBookDetails(Long bookId, BookDetailsUpdateDTO detailsDTO);
    void updateBookSalesInfo(Long bookId, BookSalesInfoUpdateDTO salesInfoDTO);
    void updateBookPackaging(Long bookId, boolean packaging);
    Book getBookById(Long bookId);
    BookDetailsDto getBookDetails(Long bookId);
    List<BookInfoDTO> getBookInfo(List<Long> bookIds);
    void notExistThrow(Long bookId);

    // aladin
    List<BookDto> searchBooks(String query, String queryType, String searchTarget, int start);
}