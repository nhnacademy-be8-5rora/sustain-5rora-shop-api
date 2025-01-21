package store.aurora.book.service.book.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.aurora.book.dto.aladin.AladinApiResponse;
import store.aurora.book.dto.aladin.AladinBookRequestDto;
import store.aurora.book.service.book.AladinBookRedisService;
import store.aurora.book.util.AladinBookClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AladinBookServiceImplTest {

    @Mock private AladinBookClient aladinBookClient;
    @Mock private ObjectMapper objectMapper;
    @Mock private AladinBookRedisService aladinBookRedisService;

    @InjectMocks
    private AladinBookServiceImpl aladinBookService;

    private AladinBookRequestDto sampleBook;
    private AladinApiResponse apiResponse;

    @BeforeEach
    void setUp() {
        sampleBook = new AladinBookRequestDto();
        sampleBook.setTitle("테스트 책");
        sampleBook.setIsbn("1234567890123");
        sampleBook.setPriceSales(15000);
        sampleBook.setPriceStandard(20000);

        apiResponse = new AladinApiResponse();
        apiResponse.setItems(List.of(sampleBook));
    }

    @Test
    @DisplayName("캐시된 검색 결과가 있을 때 Redis에서 반환")
    void searchBooks_CachedDataExists() {
        String query = "테스트";
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String cacheKey = "search:" + encodedQuery + ":type:Title:target:Book:page:1";

        when(aladinBookRedisService.getBooks(cacheKey)).thenReturn(List.of(sampleBook));

        List<AladinBookRequestDto> result = aladinBookService.searchBooks(query, "Title", "Book", 1);

        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getTitle()).isEqualTo("테스트 책");

        verify(aladinBookRedisService, never()).storeBooks(any(), any());
    }

    @Test
    @DisplayName("캐시된 데이터가 없을 때, API에서 가져와 Redis에 저장")
    void searchBooks_NoCache_FetchFromAPI() throws Exception {
        String query = "테스트";
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String cacheKey = "search:" + encodedQuery + ":type:Title:target:Book:page:1";

        when(aladinBookRedisService.getBooks(cacheKey)).thenReturn(Collections.emptyList());
        when(aladinBookClient.searchBooks(any(), any(), any(), any(), anyInt(), anyInt(), any(), any()))
                .thenReturn("{\"items\": [{\"title\": \"테스트 책\", \"isbn\": \"1234567890123\"}]}");
        when(objectMapper.readValue(anyString(), eq(AladinApiResponse.class))).thenReturn(apiResponse);

        List<AladinBookRequestDto> result = aladinBookService.searchBooks(query, "Title", "Book", 1);

        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getTitle()).isEqualTo("테스트 책");

        verify(aladinBookRedisService).storeBooks(cacheKey, result);
    }

    @Test
    @DisplayName("ISBN으로 책 조회 - 캐시된 데이터가 있을 경우 반환")
    void getBookDetailsByIsbn_CachedDataExists() {
        String isbn = "1234567890123";
        when(aladinBookRedisService.getBook("book:" + isbn)).thenReturn(sampleBook);

        AladinBookRequestDto result = aladinBookService.getBookDetailsByIsbn(isbn);

        assertThat(result).isNotNull();
        assertThat(result.getIsbn()).isEqualTo(isbn);
    }

    @Test
    @DisplayName("ISBN으로 책 조회 - 캐시가 없을 경우 API 호출 후 저장")
    void getBookDetailsByIsbn_NoCache_FetchFromAPI() throws Exception {
        String isbn = "1234567890123";

        when(aladinBookRedisService.getBook("book:" + isbn)).thenReturn(null);
        when(aladinBookClient.getBookDetails(any(), any(), any(), any(), any()))
                .thenReturn("{\"items\": [{\"title\": \"테스트 책\", \"isbn\": \"1234567890123\"}]}");
        when(objectMapper.readValue(anyString(), eq(AladinApiResponse.class))).thenReturn(apiResponse);

        AladinBookRequestDto result = aladinBookService.getBookDetailsByIsbn(isbn);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("테스트 책");

        verify(aladinBookRedisService).storeBooks("book:" + isbn, List.of(result));
    }

    @Test
    @DisplayName("API 응답이 비어 있을 경우 빈 리스트 반환")
    void getBooksFromApi_EmptyResponse() {
        when(aladinBookClient.searchBooks(any(), any(), any(), any(), anyInt(), anyInt(), any(), any()))
                .thenReturn("");
        List<AladinBookRequestDto> result = aladinBookService.searchBooks("테스트", "Title", "Book", 1);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("API 응답이 JSON 파싱 실패할 경우 빈 리스트 반환")
    void getBooksFromApi_InvalidJson() throws Exception {
        when(aladinBookClient.searchBooks(any(), any(), any(), any(), anyInt(), anyInt(), any(), any()))
                .thenReturn("INVALID JSON");
        when(objectMapper.readValue(anyString(), eq(AladinApiResponse.class))).thenThrow(JsonProcessingException.class);

        List<AladinBookRequestDto> result = aladinBookService.searchBooks("테스트", "Title", "Book", 1);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("searchBooks()에서 생성된 캐시 키 검증")
    void searchBooks_CacheKeyTest() {
        String query = "테스트";
        String cacheKey = "search:%ED%85%8C%EC%8A%A4%ED%8A%B8:type:Title:target:Book:page:1";

        when(aladinBookRedisService.getBooks(cacheKey)).thenReturn(Collections.emptyList());

        aladinBookService.searchBooks(query, "Title", "Book", 1);

        verify(aladinBookRedisService).getBooks(eq(cacheKey)); // 캐시 키가 올바르게 생성되었는지 확인
    }
}