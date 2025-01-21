package store.aurora.book.controller.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import store.aurora.book.dto.aladin.AladinBookRequestDto;
import store.aurora.book.service.book.AladinBookService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AladinBookController.class)
class AladinBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AladinBookService aladinBookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("알라딘 도서 검색 테스트 (GET /api/aladin/search)")
    void searchBooksTest() throws Exception {
        // given
        List<AladinBookRequestDto> mockBooks = List.of(
                new AladinBookRequestDto("도서 1", "저자 1", "설명 1", "출판사 1", "2024-01-01", "1234567890123"),
                new AladinBookRequestDto("도서 2", "저자 2", "설명 2", "출판사 2", "2024-01-02", "1234567890124")
        );

        when(aladinBookService.searchBooks(any(), any(), any(), anyInt())).thenReturn(mockBooks);

        // when & then
        mockMvc.perform(get("/api/aladin/search")
                        .param("query", "테스트")
                        .param("queryType", "Keyword")
                        .param("searchTarget", "Book")
                        .param("start", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 응답 확인
                .andExpect(jsonPath("$", hasSize(2))) // 검색된 도서 개수 확인
                .andExpect(jsonPath("$[0].title").value("도서 1"))
                .andExpect(jsonPath("$[1].title").value("도서 2"));

        verify(aladinBookService, times(1)).searchBooks(any(), any(), any(), anyInt());
    }

    @Test
    @DisplayName("알라딘 특정 도서 검색 테스트 (GET /api/aladin/{isbn})")
    void getBookDetailsByIsbnTest() throws Exception {
        // given
        String isbn = "1234567890123";
        AladinBookRequestDto mockBook = new AladinBookRequestDto("도서 1", "저자 1", "설명 1", "출판사 1", "2024-01-01", isbn);

        when(aladinBookService.getBookDetailsByIsbn(isbn)).thenReturn(mockBook);

        // when & then
        mockMvc.perform(get("/api/aladin/{isbn}", isbn)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 응답 확인
                .andExpect(jsonPath("$.title").value("도서 1"))
                .andExpect(jsonPath("$.isbn").value(isbn));

        verify(aladinBookService, times(1)).getBookDetailsByIsbn(isbn);
    }
}