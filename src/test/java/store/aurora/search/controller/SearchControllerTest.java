package store.aurora.search.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.aurora.book.dto.AuthorDTO;
import store.aurora.review.dto.ReviewSummaryDTO;
import store.aurora.search.dto.BookSearchResponseDTO;
import store.aurora.search.service.SearchService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SearchControllerTest {

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
    }

    @DisplayName("제목을 기준으로 검색할 때 정렬 기준에 맞게 내용과 상태코드가 잘 넘어오는지 확인")
    @Test
    void searchByTitleWithSorting_ReturnsResults() throws Exception {
        // Given
        String keyword = "Example Title";
        String type = "title";
        String orderBy = "title"; // 정렬 기준
        String orderDirection = "asc"; // 정렬 방향
        int pageNum = 1;
        PageRequest pageRequest = PageRequest.of(pageNum - 1, 8, orderDirection.equals("desc") ? Sort.by(Sort.Order.desc(orderBy)) : Sort.by(Sort.Order.asc(orderBy)));

        BookSearchResponseDTO responseDTO = new BookSearchResponseDTO(1L, "Example Title", 1000, 900, null, "Example Publisher", null, null,null,5L,new ReviewSummaryDTO(3,3.5));
        Page<BookSearchResponseDTO> page = new PageImpl<>(Collections.singletonList(responseDTO));

        when(searchService.findBooksByTitleWithDetails(keyword, pageRequest)).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/books/search")
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .param("orderBy", orderBy)
                        .param("orderDirection", orderDirection)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Example Title")));
        verify(searchService, times(1)).findBooksByTitleWithDetails(keyword, pageRequest);
    }

    @DisplayName("작가이름 기준으로 검색할 때 정렬 기준에 맞게 내용과 상태코드가 잘 넘어오는지 확인")
    @Test
    void searchByAuthorWithSorting_ReturnsResults() throws Exception {
        // Given
        String keyword = "Example Author";
        String type = "author";
        String orderBy = "title"; // 정렬 기준
        String orderDirection = "desc"; // 정렬 방향
        int pageNum = 1;
        PageRequest pageRequest = PageRequest.of(pageNum - 1, 8, orderDirection.equals("desc") ? Sort.by(Sort.Order.desc(orderBy)) : Sort.by(Sort.Order.asc(orderBy)));

        BookSearchResponseDTO responseDTO = new BookSearchResponseDTO(1L, "Example Book", 1000, 900, null, "Example Publisher", null, null,null,5L,new ReviewSummaryDTO(5,3.2));
        Page<BookSearchResponseDTO> page = new PageImpl<>(Collections.singletonList(responseDTO));

        when(searchService.findBooksByAuthorNameWithDetails(keyword, pageRequest)).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/books/search")
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .param("orderBy", orderBy)
                        .param("orderDirection", orderDirection)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Example Book")));

        verify(searchService, times(1)).findBooksByAuthorNameWithDetails(keyword, pageRequest);
    }

    @DisplayName("타입이 title,author,category에 해당하지않거나 null 또는 empty일 때 noContent 반환")
    @Test
    void searchWithInvalidType_ReturnsNoContent() throws Exception {
        // Given
        String type = "invalid";
        String keyword = "Example";
        int pageNum = 1;

        // When & Then
        mockMvc.perform(get("/api/books/search")
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verifyNoInteractions(searchService);

        type = "";
        // When & Then
        mockMvc.perform(get("/api/books/search")
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verifyNoInteractions(searchService);

        type = null;
        // When & Then
        mockMvc.perform(get("/api/books/search")
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verifyNoInteractions(searchService);
    }

    @DisplayName("키워드가 null일경우, empty인 경우 noContent 반환")
    @Test
    void searchWithNoKeyword_ReturnsNoContent() throws Exception {
        // Given
        String type = "title";
        String keyword = null;
        int pageNum = 1;

        // When & Then
        mockMvc.perform(get("/api/books/search")
                        .param("type", type)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verifyNoInteractions(searchService);

        // Given
        keyword = "";

        // When & Then
        mockMvc.perform(get("/api/books/search")
                        .param("type", type)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verifyNoInteractions(searchService);
    }

    @DisplayName("카테고리를 기준으로 검색할 때 정렬 기준에 맞게 내용과 상태코드가 잘 넘어오는지 확인")
    @Test
    void searchByCategoryWithSorting_ReturnsResults() throws Exception {
        // Given
        String keyword = "1";
        String type = "category";
        String orderBy = "title"; // 정렬 기준
        String orderDirection = "asc"; // 정렬 방향
        int pageNum = 1;
        PageRequest pageRequest = PageRequest.of(pageNum - 1, 8, orderDirection.equals("desc") ? Sort.by(Sort.Order.desc(orderBy)) : Sort.by(Sort.Order.asc(orderBy)));

        // 수정된 BookCategorySearchResponseDTO 객체 생성
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(1L);  // 카테고리 이름 추가

        List<AuthorDTO> authors = new ArrayList<>();
        authors.add(new AuthorDTO("Author Name", null));  // AuthorDTO 예시

        // 여러 개의 BookSearchResponseDTO 객체 생성
        BookSearchResponseDTO responseDTO1 = new BookSearchResponseDTO(
                1L, "Example Book 1", 1000, 900, null, "Example Publisher", null, authors, categoryIds, 5L,new ReviewSummaryDTO(102,4.1));
        BookSearchResponseDTO responseDTO2 = new BookSearchResponseDTO(
                2L, "Example Book 2", 1200, 1100, null, "Example Publisher", null, authors, categoryIds, 5L,new ReviewSummaryDTO(122,3.5));

        // Page 객체 생성
        List<BookSearchResponseDTO> responses = Arrays.asList(responseDTO1, responseDTO2);
        Page<BookSearchResponseDTO> page = new PageImpl<>(responses);

        // when
        when(searchService.findBooksByCategoryWithDetails(Long.valueOf(keyword), pageRequest)).thenReturn(page);

        // Then
        mockMvc.perform(get("/api/books/search")
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .param("orderBy", orderBy)
                        .param("orderDirection", orderDirection)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(responses.size())))  // content 배열의 크기 동적 검증
                // 첫 번째 BookSearchResponseDTO 검증
                .andExpect(jsonPath("$.content[0].id", is(responseDTO1.getId().intValue())))
                .andExpect(jsonPath("$.content[0].title", is(responseDTO1.getTitle())))
                .andExpect(jsonPath("$.content[1].id", is(responseDTO2.getId().intValue())))
                .andExpect(jsonPath("$.content[1].title", is(responseDTO2.getTitle())));

        verify(searchService, times(1)).findBooksByCategoryWithDetails(Long.valueOf(keyword), pageRequest);
    }
}
