package store.aurora.book.service;

import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.*;
import store.aurora.book.entity.Book;
import store.aurora.book.exception.BookNotFoundException;

import java.util.List;

public interface BookService {
    void saveBookWithPublisherAndSeries(BookRequestDTO requestDTO);
    void updateBookDetails(Long bookId, BookDetailsUpdateDTO detailsDTO);
    void updateBookSalesInfo(Long bookId, BookSalesInfoUpdateDTO salesInfoDTO);
    void updateBookPackaging(Long bookId, boolean packaging);
    Book getBookById(Long bookId);
    BookDetailsDto getBookDetails(Long bookId);
    List<BookInfoDTO> getBookInfo(List<Long> bookIds);
    void notExistThrow(Long bookId);
}