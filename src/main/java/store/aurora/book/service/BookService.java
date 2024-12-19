package store.aurora.book.service;

import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.*;
import store.aurora.book.entity.Book;

import java.io.IOException;
import java.util.List;

public interface BookService {
    void saveBookWithPublisherAndSeries(BookRequestDTO requestDTO);
    void updateBookDetails(Long bookId, BookDetailsUpdateDTO detailsDTO);
    void updateBookSalesInfo(Long bookId, BookSalesInfoUpdateDTO salesInfoDTO);
    void updateBookPackaging(Long bookId, boolean packaging);
    void addBookImages(Long bookId, List<MultipartFile> files) throws IOException;
    void deleteBookImage(Long bookId, Long imageId) throws IOException;
    void updateThumbnail(Long bookId, Long imageId);
    Book getBookById(Long bookId);
    BookDetailsDto getBookDetails(Long bookId);
    List<BookInfoDTO> getBookInfo(List<Long> bookIds);

}
