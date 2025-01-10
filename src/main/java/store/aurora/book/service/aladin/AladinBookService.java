package store.aurora.book.service.aladin;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.aladin.AladinBookDto;
import store.aurora.book.dto.aladin.BookRequestDto;

import java.util.List;

public interface AladinBookService {
    @Transactional
    void saveBookFromApi(BookRequestDto bookDto, List<MultipartFile> additionalImages);

    List<AladinBookDto> searchBooks(String query, String queryType, String searchTarget, int start);

    AladinBookDto getBookDetailsByIsbn(String isbn13);
}
