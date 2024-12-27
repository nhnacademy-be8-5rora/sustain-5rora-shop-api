package store.aurora.book.service;

import store.aurora.book.dto.*;
import store.aurora.book.dto.aladin.BookDetailDto;
import store.aurora.book.dto.aladin.BookDto;
import store.aurora.book.dto.aladin.BookRequestDtoEx;
import store.aurora.book.entity.Book;

import java.util.List;

public interface BookService {
    void saveBookFromApi(BookRequestDtoEx bookRequestDto);

    void saveDirectBook(BookRequestDtoEx bookRequestDto);

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