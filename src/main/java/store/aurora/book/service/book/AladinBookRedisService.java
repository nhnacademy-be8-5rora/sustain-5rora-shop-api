package store.aurora.book.service.book;

import store.aurora.book.dto.aladin.AladinBookRequestDto;

import java.util.List;

public interface AladinBookRedisService {
    void storeBooks(String cacheKey, List<AladinBookRequestDto> books);

    void storeIndividualBooks(List<AladinBookRequestDto> books);

    List<AladinBookRequestDto> getBooks(String cacheKey);

    AladinBookRequestDto getBook(String bookCacheKey);
}
