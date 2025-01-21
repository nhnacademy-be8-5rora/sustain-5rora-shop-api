package store.aurora.book.controller.publisher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import store.aurora.book.dto.publisher.PublisherRequestDto;
import store.aurora.book.dto.publisher.PublisherResponseDto;
import store.aurora.book.service.publisher.PublisherService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PublisherController.class)
class PublisherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublisherService publisherService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("출판사 목록 조회 테스트 - 페이징 검증 (GET /api/publishers)")
    void getAllPublishersPagingTest() throws Exception {
        // given
        List<PublisherResponseDto> mockPublisherList = List.of(
                new PublisherResponseDto(1L, "테스트 출판사1"),
                new PublisherResponseDto(2L, "테스트 출판사2"),
                new PublisherResponseDto(3L, "테스트 출판사3"),
                new PublisherResponseDto(4L, "테스트 출판사4"),
                new PublisherResponseDto(5L, "테스트 출판사5"),
                new PublisherResponseDto(6L, "테스트 출판사6")
        );
        Page<PublisherResponseDto> mockPage = new PageImpl<>(mockPublisherList.subList(0, 5), PageRequest.of(0, 5), 6); // 총 6개 중 첫 페이지 5개

        given(publisherService.getAllPublishers(any(PageRequest.class))).willReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/publishers")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 응답 확인
                .andExpect(jsonPath("$.content", hasSize(5))) // 한 페이지에 5개
                .andExpect(jsonPath("$.totalElements").value(6)) // 전체 데이터 개수 6개
                .andExpect(jsonPath("$.totalPages").value(2)) // 총 페이지 수 2개
                .andExpect(jsonPath("$.content[0].id").value(1L)) // 첫 번째 출판사 확인
                .andExpect(jsonPath("$.content[4].id").value(5L)); // 다섯 번째 출판사 확인

        verify(publisherService, times(1)).getAllPublishers(any(PageRequest.class));
    }

    @Test
    @DisplayName("출판사 단건 조회 테스트 (GET /api/publishers/{publisher-id})")
    void getPublisherByIdTest() throws Exception {
        // given
        Long publisherId = 1L;
        PublisherResponseDto responseDto = new PublisherResponseDto(publisherId, "테스트 출판사1");
        given(publisherService.getPublisherById(publisherId)).willReturn(responseDto);

        // when & then
        mockMvc.perform(get("/api/publishers/{publisher-id}", publisherId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 응답 확인
                .andExpect(jsonPath("$.id").value(publisherId))
                .andExpect(jsonPath("$.name").value("테스트 출판사1"));

        verify(publisherService, times(1)).getPublisherById(publisherId);
    }

    @Test
    @DisplayName("출판사 등록 테스트 (POST /api/publishers)")
    void createPublisherTest() throws Exception {
        // given
        PublisherRequestDto requestDto = new PublisherRequestDto("테스트 출판사 생성");

        doNothing().when(publisherService).createPublisher(any(PublisherRequestDto.class));

        // when & then
        mockMvc.perform(post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated()); // HTTP 201 응답 확인

        verify(publisherService, times(1)).createPublisher(any(PublisherRequestDto.class));
    }

    @Test
    @DisplayName("출판사 수정 테스트 (PUT /api/publishers/{publisher-id})")
    void updatePublisherTest() throws Exception {
        // given
        Long publisherId = 1L;
        PublisherRequestDto requestDto = new PublisherRequestDto("테스트 출판사 수정");

        doNothing().when(publisherService).updatePublisher(eq(publisherId), any(PublisherRequestDto.class));

        // when & then
        mockMvc.perform(put("/api/publishers/{publisher-id}", publisherId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNoContent()); // HTTP 204 응답 확인

        verify(publisherService, times(1)).updatePublisher(eq(publisherId), any(PublisherRequestDto.class));
    }

    @Test
    @DisplayName("출판사 삭제 테스트 (DELETE /api/publishers/{publisher-id})")
    void deletePublisherTest() throws Exception {
        // given
        Long publisherId = 1L;
        doNothing().when(publisherService).deletePublisher(publisherId);

        // when & then
        mockMvc.perform(delete("/api/publishers/{publisher-id}", publisherId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // HTTP 204 응답 확인

        verify(publisherService, times(1)).deletePublisher(publisherId);
    }
}