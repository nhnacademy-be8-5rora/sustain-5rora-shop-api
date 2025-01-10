package store.aurora.book.service.aladin.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import store.aurora.book.dto.aladin.AladinBookDto;
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
    public void storeBooks(String cacheKey, List<AladinBookDto> books) {
        if (CollectionUtils.isEmpty(books)) {
            return; // 데이터가 없으면 저장하지 않음
        }
        // 리스트 전체 저장
        redisCacheManager.store(cacheKey, books, CACHE_DURATION);

        // 각 책 데이터를 개별적으로 저장
        storeIndividualBooks(books);
    }

    @Override
    public void storeIndividualBooks(List<AladinBookDto> books) {
        books.stream()
                .filter(book -> book.getIsbn13() != null) // ISBN이 존재하는 책만 저장
                .forEach(book -> {
                    String bookCacheKey = "book:" + book.getIsbn13();
                    redisCacheManager.store(bookCacheKey, book, CACHE_DURATION);
                });
    }

    @Override
    public List<AladinBookDto> getBooks(String cacheKey) {
        return redisCacheManager.get(cacheKey, new TypeReference<>() {});
    }

    @Override
    public AladinBookDto getBook(String bookCacheKey) {
        return redisCacheManager.get(bookCacheKey, new TypeReference<>() {});
    }
}
