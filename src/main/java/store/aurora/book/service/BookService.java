package store.aurora.book.service;

import store.aurora.book.dto.*;
import store.aurora.book.entity.Book;

import java.util.List;

public interface BookService {
    Book saveBookWithPublisherAndSeries(BookRequestDTO requestDTO);
    Book updateBookDetails(Long bookId, BookDetailsUpdateDTO detailsDTO);
    Book updateBookSalesInfo(Long bookId, BookSalesInfoDTO salesInfoDTO);
    Book updateBookPackaging(Long bookId, boolean packaging);
    Book getBookById(Long bookId);
    BookDetailsDto getBookDetails(Long bookId);
    List<BookInfoDTO> getBookInfo(List<Long> bookIds);
}
