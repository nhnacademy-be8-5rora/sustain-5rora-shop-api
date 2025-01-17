package store.aurora.book.service.aladin.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import store.aurora.book.dto.aladin.AladinBookRequestDto;
import store.aurora.book.service.aladin.AladinBookRedisService;
import store.aurora.book.util.RedisCacheManager;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AladinBookRedisServiceImpl implements AladinBookRedisService {

    private final RedisCacheManager redisCacheManager;

    private static final Duration CACHE_DURATION = Duration.ofMinutes(30);

    @Override
    public void storeBooks(String cacheKey, List<AladinBookRequestDto> books) {
        if (CollectionUtils.isEmpty(books)) {
            return; // 데이터가 없으면 저장하지 않음
        }
        // 리스트 전체 저장
        redisCacheManager.store(cacheKey, books, CACHE_DURATION);

        // 각 책 데이터를 개별적으로 저장
        storeIndividualBooks(books);
    }

    @Override
    public void storeIndividualBooks(List<AladinBookRequestDto> books) {
        books.stream()
                .filter(book -> book.getValidIsbn() != null && !book.getValidIsbn().isBlank()) // 유효한 ISBN이 있는 경우만 저장
                .forEach(book -> {
                    String bookCacheKey = "book:" + book.getValidIsbn();
                    redisCacheManager.store(bookCacheKey, book, CACHE_DURATION);
                });
    }

    @Override
    public List<AladinBookRequestDto> getBooks(String cacheKey) {
        return redisCacheManager.get(cacheKey, new TypeReference<>() {});
    }

    @Override
    public AladinBookRequestDto getBook(String bookCacheKey) {
        return redisCacheManager.get(bookCacheKey, new TypeReference<>() {});
    }
}
