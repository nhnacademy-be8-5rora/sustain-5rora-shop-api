package store.aurora.search.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import store.aurora.search.dto.BookSearchResponseDTO;
import store.aurora.search.service.ElasticSearchService;
import store.aurora.book.dto.AuthorDTO;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ElasticSearchController.class)
class ElasticSearchControllerTest {
    @Autowired
    private MockMvc mockMvc;  // MockMvc는 @WebMvcTest에서 자동으로 설정됩니다.

    @MockBean
    private ElasticSearchService elasticSearchService;  // @MockBean으로 ElasticSearchService를 Mock 처리

    @Test
    @DisplayName("성공적인 책 검색 결과 반환")
    void testGetBooks_Success() throws Exception {
        // Given
        String type = "fullText";
        String keyword = "포켓몬스터";
        int pageNum = 0;
        String userId = "user123";

        // Set up authors
        AuthorDTO author1 = new AuthorDTO("john doe", "Author");
        AuthorDTO author2 = new AuthorDTO("doe john", "Author");

        // Create BookSearchResponseDTO
        BookSearchResponseDTO book1 = new BookSearchResponseDTO(1L, "포켓몬스터 1", 10000, 8000,
                LocalDate.of(2022, 5, 10), "Publisher 1", "img1.jpg",
                List.of(author1), Arrays.asList(1L, 2L), 100L, 10, 4.5, false, true);

        BookSearchResponseDTO book2 = new BookSearchResponseDTO(2L, "포켓몬스터 2", 12000, 10000,
                LocalDate.of(2023, 1, 15), "Publisher 2", "img2.jpg",
                List.of(author2), Arrays.asList(2L, 3L), 150L, 20, 4.0, true, false);

        // Create a Page of BookSearchResponseDTO
        Page<BookSearchResponseDTO> page = new PageImpl<>(Arrays.asList(book1, book2), PageRequest.of(pageNum , 8), 2);

        // Mock service to return the Page
        when(elasticSearchService.searchBooks(type, keyword, PageRequest.of(pageNum , 8), userId)).thenReturn(page);

        // Create a custom ObjectMapper to handle the LocalDate format
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();  // This is important for LocalDate handling

        // When & Then
        mockMvc.perform(get("/api/books/search/elastic-search")
                        .header("X-USER-ID", userId)
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(page)))  // Use ObjectMapper to serialize the response
                .andExpect(status().isOk())  // Expect 200 OK
                .andExpect(jsonPath("$.content[0].title").value("포켓몬스터 1"))
                .andExpect(jsonPath("$.content[1].title").value("포켓몬스터 2"))
                .andExpect(jsonPath("$.content[0].authors[0].name").value("john doe"))
                .andExpect(jsonPath("$.content[1].authors[0].name").value("doe john"));
    }

    @Test
    @DisplayName("잘못된 페이지 번호로 인한 400 오류 반환")
    void testSearchBooks_BadRequest_InvalidPageNum() throws Exception {
        // Given
        int invalidPageNum = -1;

        // When & Then
        mockMvc.perform(get("/api/books/search/elastic-search")
                        .param("pageNum", String.valueOf(invalidPageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("검색 결과가 없을 때 204 No Content 반환 (null 반환)")
    void testSearchBooks_NoContent_NullPage() throws Exception {
        // Given
        String type = "fullText";
        String keyword = "포켓몬스터";
        int pageNum = 0;
        String userId = "user123";

        // Mock service to return null
        when(elasticSearchService.searchBooks(type, keyword, PageRequest.of(pageNum , 8), userId))
                .thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/books/search/elastic-search")
                        .header("X-USER-ID", userId)
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());  // Expect 204 No Content
    }

    @Test
    @DisplayName("검색 결과가 없을 때 204 No Content 반환 (empty 반환)")
    void testSearchBooks_NoContent_EmptyPage() throws Exception {
        // Given
        String type = "fullText";
        String keyword = "포켓몬스터";
        int pageNum = 0;
        String userId = "user123";

        // Mock service to return null
        when(elasticSearchService.searchBooks(type, keyword, PageRequest.of(pageNum , 8), userId))
                .thenReturn(Page.empty());

        // When & Then
        mockMvc.perform(get("/api/books/search/elastic-search")
                        .header("X-USER-ID", userId)
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());  // Expect 204 No Content
    }

    @Test
    @DisplayName("성공적인 동기화")
    void testSyncBooks_Success() throws Exception {
        // Given
        long count = 10L;  // 성공적으로 동기화된 책의 개수
        when(elasticSearchService.saveBooksNotInElasticSearch()).thenReturn(count);  // mock 처리

        // When & Then
        mockMvc.perform(post("/api/books/search/elastic-search/sync")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Expect 200 OK
                .andExpect(jsonPath("$").value(count));  // Return the count of synced books
    }

    @Test
    @DisplayName("동기화 실패")
    void testSyncBooks_Failure() throws Exception {
        // Given
        when(elasticSearchService.saveBooksNotInElasticSearch()).thenThrow(new RuntimeException("Sync failed"));

        // When & Then
        mockMvc.perform(post("/api/books/search/elastic-search/sync")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());  // Expect 400 Bad Request on failure
    }
}
