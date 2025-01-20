package store.aurora.book.service.book;

import store.aurora.book.dto.aladin.AladinBookRequestDto;

import java.util.List;

public interface AladinBookService {
    List<AladinBookRequestDto> searchBooks(String query, String queryType, String searchTarget, int start);

    AladinBookRequestDto getBookDetailsByIsbn(String isbn);
}