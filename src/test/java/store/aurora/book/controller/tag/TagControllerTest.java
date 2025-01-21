package store.aurora.book.controller.tag;

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
import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.book.dto.tag.TagResponseDto;
import store.aurora.book.service.tag.TagService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TagController.class)
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("태그 목록 조회 테스트 - 페이징 검증 (GET /api/tags)")
    void getAllTagsTest() throws Exception {
        // given (6개의 데이터로 페이징 테스트)
        List<TagResponseDto> mockTagList = List.of(
                new TagResponseDto(1L, "태그 1"),
                new TagResponseDto(2L, "태그 2"),
                new TagResponseDto(3L, "태그 3"),
                new TagResponseDto(4L, "태그 4"),
                new TagResponseDto(5L, "태그 5"),
                new TagResponseDto(6L, "태그 6") // 6번째 데이터 (2페이지로 넘어감)
        );

        Page<TagResponseDto> mockPage = new PageImpl<>(mockTagList.subList(0, 5), PageRequest.of(0, 5), 6);

        given(tagService.getTags(any(PageRequest.class))).willReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/tags")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 응답 확인
                .andExpect(jsonPath("$.content", hasSize(5))) // 한 페이지에 5개만 있어야 함
                .andExpect(jsonPath("$.totalElements").value(6)) // 전체 데이터 개수 6개
                .andExpect(jsonPath("$.totalPages").value(2)) // 총 페이지 수 2개
                .andExpect(jsonPath("$.content[0].id").value(1L)) // 첫 번째 태그 확인
                .andExpect(jsonPath("$.content[4].id").value(5L)); // 다섯 번째 태그 확인 (한 페이지의 마지막 데이터)

        verify(tagService, times(1)).getTags(any(PageRequest.class));
    }

    @Test
    @DisplayName("특정 태그 조회 테스트 (GET /api/tags/{tag-id})")
    void getTagByIdTest() throws Exception {
        // given
        Long tagId = 1L;
        TagResponseDto mockTag = new TagResponseDto(tagId, "태그 1");
        given(tagService.getTagById(tagId)).willReturn(mockTag);

        // when & then
        mockMvc.perform(get("/api/tags/{tag-id}", tagId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 응답 확인
                .andExpect(jsonPath("$.id").value(tagId))
                .andExpect(jsonPath("$.name").value("태그 1"));

        verify(tagService, times(1)).getTagById(tagId);
    }

    @Test
    @DisplayName("태그 생성 테스트 (POST /api/tags)")
    void createTagTest() throws Exception {
        // given
        TagRequestDto requestDto = new TagRequestDto("새 태그");
        TagResponseDto responseDto = new TagResponseDto(1L, "새 태그");

        when(tagService.createTag(any(TagRequestDto.class))).thenReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated()) // HTTP 201 응답 확인
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("새 태그"));

        verify(tagService, times(1)).createTag(any(TagRequestDto.class));
    }

    @Test
    @DisplayName("태그 수정 테스트 (PUT /api/tags/{tag-id})")
    void updateTagTest() throws Exception {
        // given
        Long tagId = 1L;
        TagRequestDto requestDto = new TagRequestDto("수정된 태그");
        TagResponseDto responseDto = new TagResponseDto(tagId, "수정된 태그");

        when(tagService.updateTag(eq(tagId), any(TagRequestDto.class))).thenReturn(responseDto);

        // when & then
        mockMvc.perform(put("/api/tags/{tag-id}", tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk()) // HTTP 200 응답 확인
                .andExpect(jsonPath("$.id").value(tagId))
                .andExpect(jsonPath("$.name").value("수정된 태그"));

        verify(tagService, times(1)).updateTag(eq(tagId), any(TagRequestDto.class));
    }

    @Test
    @DisplayName("태그 삭제 테스트 (DELETE /api/tags/{tag-id})")
    void deleteTagTest() throws Exception {
        // given
        Long tagId = 1L;
        doNothing().when(tagService).deleteTag(tagId);

        // when & then
        mockMvc.perform(delete("/api/tags/{tag-id}", tagId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // HTTP 204 응답 확인

        verify(tagService, times(1)).deleteTag(tagId);
    }

}