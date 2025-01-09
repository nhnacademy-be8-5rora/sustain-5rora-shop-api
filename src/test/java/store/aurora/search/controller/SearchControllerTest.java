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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.aurora.book.dto.AuthorDTO;
import store.aurora.search.dto.BookSearchResponseDTO;
import store.aurora.search.service.SearchService;

import java.time.LocalDate;
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

    private static final String BASE_URL = "/api/books/search";
    private static final int PAGE_SIZE = 8;

    private PageRequest createPageRequest(int pageNum) {
        return PageRequest.of(pageNum - 1, PAGE_SIZE);
    }

    @DisplayName("제목을 기준으로 검색할 때 내용과 상태코드가 잘 넘어오는지 확인")
    @Test
    void searchByTitle_ReturnsResults() throws Exception {
        String keyword = "Example Title";
        String type = "title";
        int pageNum = 1;
        List<AuthorDTO> authors = Arrays.asList(new AuthorDTO("John Doe", "Writer"));
        PageRequest pageRequest = createPageRequest(pageNum).withSort(Sort.by(Sort.Order.asc("id"))); // 정렬을 명시적으로 설정
        BookSearchResponseDTO responseDTO = new BookSearchResponseDTO(1L, "Example Title", 1000, 900, LocalDate.now(), "Example Publisher", "test.jpeg", authors, null, 5L, 3, 3.5, true, true);
        Page<BookSearchResponseDTO> page = new PageImpl<>(Collections.singletonList(responseDTO));

        // searchService의 예외 처리를 추가하고 예외 발생 시 적절한 메시지를 던지도록 수정
        when(searchService.findBooksByTitleWithDetails(any(), eq(keyword), eq(pageRequest)))
                .thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Example Title")));

        verify(searchService, times(1)).findBooksByTitleWithDetails(any(), eq(keyword), eq(pageRequest));
    }


    @DisplayName("작가이름 기준으로 검색할 때 내용과 상태코드가 잘 넘어오는지 확인")
    @Test
    void searchByAuthor_ReturnsResults() throws Exception {
        String keyword = "Example Author";
        String type = "author";
        int pageNum = 1;
        PageRequest pageRequest = createPageRequest(pageNum).withSort(Sort.by(Sort.Order.asc("id"))); // 정렬을 명시적으로 설정
        BookSearchResponseDTO responseDTO = new BookSearchResponseDTO(1L, "Example Book", 1000, 900, null, "Example Publisher", null, null, null, 5L, 3, 3.5,true,true);
        Page<BookSearchResponseDTO> page = new PageImpl<>(Collections.singletonList(responseDTO));

        when(searchService.findBooksByAuthorNameWithDetails(null,keyword, pageRequest)).thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Example Book")));

        verify(searchService, times(1)).findBooksByAuthorNameWithDetails(null,keyword, pageRequest);
    }

    @DisplayName("타입이 title,author,category에 해당하지 않거나 null 또는 empty일 때 noContent 반환")
    @Test
    void searchWithInvalidType_ReturnsNoContent() throws Exception {
        String type = "invalid";
        String keyword = "Example";
        int pageNum = 1;

        mockMvc.perform(get(BASE_URL)
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verifyNoInteractions(searchService);

        type = "";
        mockMvc.perform(get(BASE_URL)
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verifyNoInteractions(searchService);

        type = null;
        mockMvc.perform(get(BASE_URL)
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verifyNoInteractions(searchService);
    }

    @DisplayName("키워드가 null일 경우, empty인 경우 noContent 반환")
    @Test
    void searchWithNoKeyword_ReturnsNoContent() throws Exception {
        String type = "title";
        String keyword = null;
        int pageNum = 1;

        mockMvc.perform(get(BASE_URL)
                        .param("type", type)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verifyNoInteractions(searchService);

        keyword = "";
        mockMvc.perform(get(BASE_URL)
                        .param("type", type)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verifyNoInteractions(searchService);
    }

    @DisplayName("카테고리를 기준으로 검색할 때 내용과 상태코드가 잘 넘어오는지 확인")
    @Test
    void searchByCategory_ReturnsResults() throws Exception {
        String keyword = "1";
        String type = "category";
        int pageNum = 1;
        PageRequest pageRequest = createPageRequest(pageNum).withSort(Sort.by(Sort.Order.asc("id"))); // 정렬을 명시적으로 설정

        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(1L);

        List<AuthorDTO> authors = new ArrayList<>();
        authors.add(new AuthorDTO("Author Name", null));

        BookSearchResponseDTO responseDTO1 = new BookSearchResponseDTO(1L, "Example Book 1", 1000, 900, null, "Example Publisher", null, authors, categoryIds, 5L, 3, 3.5,false,true);
        BookSearchResponseDTO responseDTO2 = new BookSearchResponseDTO(2L, "Example Book 2", 1200, 1100, null, "Example Publisher", null, authors, categoryIds, 5L, 3, 3.5,false,true);

        List<BookSearchResponseDTO> responses = Arrays.asList(responseDTO1, responseDTO2);
        Page<BookSearchResponseDTO> page = new PageImpl<>(responses);

        when(searchService.findBooksByCategoryWithDetails(null,Long.valueOf(keyword), pageRequest)).thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(responses.size())))
                .andExpect(jsonPath("$.content[0].id", is(responseDTO1.getId().intValue())))
                .andExpect(jsonPath("$.content[0].title", is(responseDTO1.getTitle())))
                .andExpect(jsonPath("$.content[0].publisherName", is(responseDTO1.getPublisherName())))
                .andExpect(jsonPath("$.content[0].categoryIdList[0]", is(1)))
                .andExpect(jsonPath("$.content[0].authors[0].name", is(responseDTO1.getAuthors().get(0).getName())))
                .andExpect(jsonPath("$.content[1].id", is(responseDTO2.getId().intValue())))
                .andExpect(jsonPath("$.content[1].title", is(responseDTO2.getTitle())))
                .andExpect(jsonPath("$.content[1].publisherName", is(responseDTO2.getPublisherName())))
                .andExpect(jsonPath("$.content[1].categoryIdList[0]", is(1)))
                .andExpect(jsonPath("$.content[1].authors[0].name", is(responseDTO2.getAuthors().get(0).getName())));

        verify(searchService, times(1)).findBooksByCategoryWithDetails(null,Long.valueOf(keyword), pageRequest);
    }

    @DisplayName("정렬 기준(orderBy)과 방향(orderDirection) 확인")
    @Test
    void searchWithOrderByAndDirection_ReturnsSortedResults() throws Exception {
        String keyword = "Example";
        String type = "title";
        int pageNum = 1;
        String orderBy = "salePrice";
        String orderDirection = "desc"; // 내림차순 정렬
        PageRequest pageRequest = createPageRequest(pageNum).withSort(Sort.by(Sort.Order.desc(orderBy))); // 내림차순 정렬

        BookSearchResponseDTO responseDTO = new BookSearchResponseDTO(1L, "Example Title", 1000, 900, LocalDate.now(), "Example Publisher", "test.jpeg", null, null, 5L, 3, 3.5, true, true);
        Page<BookSearchResponseDTO> page = new PageImpl<>(Collections.singletonList(responseDTO));

        // searchService의 예외 처리를 추가하고 예외 발생 시 적절한 메시지를 던지도록 수정
        when(searchService.findBooksByTitleWithDetails(any(), eq(keyword), eq(pageRequest)))
                .thenReturn(page);

        mockMvc.perform(get(BASE_URL)
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

        verify(searchService, times(1)).findBooksByTitleWithDetails(any(), eq(keyword), eq(pageRequest));
    }

    @DisplayName("정렬 기준(orderBy)이 empty인경우 기본값 적용")
    @Test
    void searchWithDefaultOrderBy_ReturnsResults() throws Exception {
        String keyword = "Example";
        String type = "title";
        int pageNum = 1;
        PageRequest pageRequest = createPageRequest(pageNum).withSort(Sort.by(Sort.Order.asc("id"))); // 기본값 "id"로 오름차순 정렬

        BookSearchResponseDTO responseDTO = new BookSearchResponseDTO(1L, "Example Title", 1000, 900, LocalDate.now(), "Example Publisher", "test.jpeg", null, null, 5L, 3, 3.5, true, true);
        Page<BookSearchResponseDTO> page = new PageImpl<>(Collections.singletonList(responseDTO));

        when(searchService.findBooksByTitleWithDetails(any(), eq(keyword), eq(pageRequest)))
                .thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .param("orderBy","")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Example Title")));

        verify(searchService, times(1)).findBooksByTitleWithDetails(any(), eq(keyword), eq(pageRequest));
    }

    @DisplayName("카테고리 검색 시 keyword가 비어있으면 기본값 '0'으로 설정되는지 확인")
    @Test
    void searchByCategoryWithEmptyKeyword_SetsDefaultKeyword() throws Exception {
        String keyword = ""; // 비어있는 키워드
        String type = "category";
        int pageNum = 1;
        PageRequest pageRequest = createPageRequest(pageNum).withSort(Sort.by(Sort.Order.asc("id"))); // 정렬을 명시적으로 설정

        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(1L);

        List<AuthorDTO> authors = new ArrayList<>();
        authors.add(new AuthorDTO("Author Name", null));

        BookSearchResponseDTO responseDTO1 = new BookSearchResponseDTO(1L, "Example Book 1", 1000, 900, null, "Example Publisher", null, authors, categoryIds, 5L, 3, 3.5, false, true);
        BookSearchResponseDTO responseDTO2 = new BookSearchResponseDTO(2L, "Example Book 2", 1200, 1100, null, "Example Publisher", null, authors, categoryIds, 5L, 3, 3.5, false, true);

        List<BookSearchResponseDTO> responses = Arrays.asList(responseDTO1, responseDTO2);
        Page<BookSearchResponseDTO> page = new PageImpl<>(responses);

        // keyword가 비어 있으면 "0"으로 설정되도록 mock 설정
        when(searchService.findBooksByCategoryWithDetails(null, 0L, pageRequest)).thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .param("type", type)
                        .param("keyword", keyword) // 빈 값
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(responses.size())))
                .andExpect(jsonPath("$.content[0].id", is(responseDTO1.getId().intValue())))
                .andExpect(jsonPath("$.content[0].title", is(responseDTO1.getTitle())))
                .andExpect(jsonPath("$.content[0].publisherName", is(responseDTO1.getPublisherName())))
                .andExpect(jsonPath("$.content[0].categoryIdList[0]", is(1)))
                .andExpect(jsonPath("$.content[0].authors[0].name", is(responseDTO1.getAuthors().get(0).getName())))
                .andExpect(jsonPath("$.content[1].id", is(responseDTO2.getId().intValue())))
                .andExpect(jsonPath("$.content[1].title", is(responseDTO2.getTitle())))
                .andExpect(jsonPath("$.content[1].publisherName", is(responseDTO2.getPublisherName())))
                .andExpect(jsonPath("$.content[1].categoryIdList[0]", is(1)))
                .andExpect(jsonPath("$.content[1].authors[0].name", is(responseDTO2.getAuthors().get(0).getName())));

        verify(searchService, times(1)).findBooksByCategoryWithDetails(null, 0L, pageRequest);
    }

    @DisplayName("카테고리 검색 시 잘못된 카테고리 ID 입력 시 BadRequest 반환")
    @Test
    void searchWithInvalidCategoryId_ReturnsBadRequest() throws Exception {
        String type = "category";
        String keyword = "invalid";  // 잘못된 카테고리 ID 입력
        int pageNum = 1;

        mockMvc.perform(get(BASE_URL)
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // 잘못된 요청이므로 400 상태코드

        verifyNoInteractions(searchService); // 예외 발생 시 searchService 호출되지 않음
    }

    @DisplayName("잘못된 pageNum 입력 시 BadRequest 반환")
    @Test
    void searchWithInvalidPageNumReturnsBadRequest() throws Exception {
        String type = "title";
        String keyword = "t";  // 잘못된 카테고리 ID 입력
        String pageNum = "a";

        mockMvc.perform(get(BASE_URL)
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // 잘못된 요청이므로 400 상태코드

        verifyNoInteractions(searchService); // 예외 발생 시 searchService 호출되지 않음
    }

}
