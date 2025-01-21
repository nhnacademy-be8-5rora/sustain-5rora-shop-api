package store.aurora.book.controller.author;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import store.aurora.book.dto.author.AuthorRequestDto;
import store.aurora.book.dto.author.AuthorResponseDto;
import store.aurora.book.service.author.AuthorService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("저자 목록 조회 테스트 - 페이징 검증 (GET /api/authors)")
    void getAllAuthorsPagingTest() throws Exception {
        // given
        List<AuthorResponseDto> mockAuthorList = List.of(
                new AuthorResponseDto(1L, "테스트 작가1"),
                new AuthorResponseDto(2L, "테스트 작가2"),
                new AuthorResponseDto(3L, "테스트 작가3"),
                new AuthorResponseDto(4L, "테스트 작가4"),
                new AuthorResponseDto(5L, "테스트 작가5"),
                new AuthorResponseDto(6L, "테스트 작가6")
        );
        Page<AuthorResponseDto> mockPage = new PageImpl<>(mockAuthorList.subList(0, 5), PageRequest.of(0, 5), 6); // 첫 페이지(0)에서 5개만 반환

        given(authorService.getAllAuthors(any(PageRequest.class))).willReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/authors")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 응답 확인
                .andExpect(jsonPath("$.content", hasSize(5))) // 한 페이지에 5개
                .andExpect(jsonPath("$.totalElements").value(6)) // 전체 데이터 개수 6개
                .andExpect(jsonPath("$.totalPages").value(2)) // 총 페이지 수 2개
                .andExpect(jsonPath("$.content[0].id").value(1L)) // 첫 번째 저자 확인
                .andExpect(jsonPath("$.content[4].id").value(5L)); // 다섯 번째 저자 확인

        verify(authorService, times(1)).getAllAuthors(any(PageRequest.class));
    }

    @Test
    @DisplayName("저자 단건 조회 테스트 (GET /api/authors/{author-id})")
    void getAuthorByIdTest() throws Exception {
        // given
        Long authorId = 1L;
        AuthorResponseDto responseDto = new AuthorResponseDto(authorId, "테스트 작가1");
        given(authorService.getAuthorById(authorId)).willReturn(responseDto);

        // when & then
        mockMvc.perform(get("/api/authors/{author-id}", authorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 응답 확인
                .andExpect(jsonPath("$.id").value(authorId))
                .andExpect(jsonPath("$.name").value("테스트 작가1"));

        verify(authorService, times(1)).getAuthorById(authorId);
    }

    @Test
    @DisplayName("저자 등록 테스트 (POST /api/authors)")
    void createAuthorTest() throws Exception {
        // given
        AuthorRequestDto requestDto = new AuthorRequestDto("테스트 작가 생성");

        doNothing().when(authorService).createAuthor(any(AuthorRequestDto.class));

        // when & then
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated()); // HTTP 201 응답 확인

        verify(authorService, times(1)).createAuthor(any(AuthorRequestDto.class));
    }

    @Test
    @DisplayName("저자 수정 테스트 (PUT /api/authors/{author-id})")
    void updateAuthorTest() throws Exception {
        // given
        Long authorId = 1L;
        AuthorRequestDto requestDto = new AuthorRequestDto("테스트 작가 수정");

        doNothing().when(authorService).updateAuthor(eq(authorId), any(AuthorRequestDto.class));

        // when & then
        mockMvc.perform(put("/api/authors/{author-id}", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNoContent()); // HTTP 204 응답 확인

        verify(authorService, times(1)).updateAuthor(eq(authorId), any(AuthorRequestDto.class));
    }

    @Test
    @DisplayName("저자 삭제 테스트 (DELETE /api/authors/{author-id})")
    void deleteAuthorTest() throws Exception {
        // given
        Long authorId = 1L;
        doNothing().when(authorService).deleteAuthor(authorId);

        // when & then
        mockMvc.perform(delete("/api/authors/{author-id}", authorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // HTTP 204 응답 확인

        verify(authorService, times(1)).deleteAuthor(authorId);
    }
}