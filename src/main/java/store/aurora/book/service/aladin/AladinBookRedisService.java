package store.aurora.book.service.aladin;

import store.aurora.book.dto.aladin.AladinBookDto;

import java.util.List;

public interface AladinBookRedisService {
    void storeBooks(String cacheKey, List<AladinBookDto> books);

    void storeIndividualBooks(List<AladinBookDto> books);

    List<AladinBookDto> getBooks(String cacheKey);

    AladinBookDto getBook(String bookCacheKey);
}
