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
        PageRequest pageRequest = createPageRequest(pageNum).withSort(Sort.by(Sort.Order.asc("id"))); // 정렬을 명시적으로 설정
        BookSearchResponseDTO responseDTO = new BookSearchResponseDTO(1L, "Example Title", 1000, 900, null, "Example Publisher", null, null, null, 5L, 3, 3.5,true);
        Page<BookSearchResponseDTO> page = new PageImpl<>(Collections.singletonList(responseDTO));

        when(searchService.findBooksByTitleWithDetails(null,keyword, pageRequest)).thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .param("type", type)
                        .param("keyword", keyword)
                        .param("pageNum", String.valueOf(pageNum))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Example Title")));

        verify(searchService, times(1)).findBooksByTitleWithDetails(null,keyword, pageRequest);
    }

    @DisplayName("작가이름 기준으로 검색할 때 내용과 상태코드가 잘 넘어오는지 확인")
    @Test
    void searchByAuthor_ReturnsResults() throws Exception {
        String keyword = "Example Author";
        String type = "author";
        int pageNum = 1;
        PageRequest pageRequest = createPageRequest(pageNum).withSort(Sort.by(Sort.Order.asc("id"))); // 정렬을 명시적으로 설정
        BookSearchResponseDTO responseDTO = new BookSearchResponseDTO(1L, "Example Book", 1000, 900, null, "Example Publisher", null, null, null, 5L, 3, 3.5,true);
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

        BookSearchResponseDTO responseDTO1 = new BookSearchResponseDTO(1L, "Example Book 1", 1000, 900, null, "Example Publisher", null, authors, categoryIds, 5L, 3, 3.5,false);
        BookSearchResponseDTO responseDTO2 = new BookSearchResponseDTO(2L, "Example Book 2", 1200, 1100, null, "Example Publisher", null, authors, categoryIds, 5L, 3, 3.5,false);

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
}
