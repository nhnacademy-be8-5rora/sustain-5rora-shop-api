package store.aurora.book.controller.series;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import store.aurora.book.dto.series.SeriesRequestDto;
import store.aurora.book.dto.series.SeriesResponseDto;
import store.aurora.book.service.series.SeriesService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SeriesController.class)
class SeriesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeriesService seriesService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("시리즈 목록 조회 테스트 - 페이징 검증 (GET /api/series)")
    void getAllSeriesTest() throws Exception {
        // given (Mock 데이터 설정)
        List<SeriesResponseDto> mockSeriesList = List.of(
                new SeriesResponseDto(1L, "테스트 시리즈 1"),
                new SeriesResponseDto(2L, "테스트 시리즈 2"),
                new SeriesResponseDto(3L, "테스트 시리즈 3"),
                new SeriesResponseDto(4L, "테스트 시리즈 4"),
                new SeriesResponseDto(5L, "테스트 시리즈 5"),
                new SeriesResponseDto(6L, "테스트 시리즈 6") // 6번째 데이터 (2페이지로 넘어감)
        );

        PageImpl<SeriesResponseDto> mockPage = new PageImpl<>(mockSeriesList.subList(0, 5), PageRequest.of(0, 5), 6); // 첫 페이지(0)에서 5개만 반환

        when(seriesService.getAllSeries(any(PageRequest.class))).thenReturn(mockPage);

        // when & then (MockMvc 실행)
        mockMvc.perform(get("/api/series")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200 OK 확인
                .andExpect(jsonPath("$.content", hasSize(5))) // 한 페이지에 5개만 있어야 함
                .andExpect(jsonPath("$.totalElements").value(6)) // 전체 데이터 개수 6개
                .andExpect(jsonPath("$.totalPages").value(2)) // 총 페이지 수 2개
                .andExpect(jsonPath("$.content[0].id").value(1L)) // 첫 번째 시리즈 확인
                .andExpect(jsonPath("$.content[4].id").value(5L)); // 다섯 번째 시리즈 확인 (한 페이지의 마지막 데이터)

        verify(seriesService, times(1)).getAllSeries(any(PageRequest.class));
    }

    @Test
    @DisplayName("특정 시리즈 조회 테스트 (GET /api/series/{id})")
    void getSeriesByIdTest() throws Exception {
        // given
        SeriesResponseDto mockSeries = new SeriesResponseDto(1L, "테스트 시리즈 1");
        when(seriesService.getSeriesById(1L)).thenReturn(mockSeries);

        // when & then
        mockMvc.perform(get("/api/series/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("테스트 시리즈 1"));

        verify(seriesService, times(1)).getSeriesById(1L);
    }

    @Test
    @DisplayName("시리즈 생성 테스트 (POST /api/series)")
    void createSeriesTest() throws Exception {
        // given
        SeriesRequestDto requestDto = new SeriesRequestDto("테스트 시리즈 생성");
        doNothing().when(seriesService).createSeries(any());

        // when & then
        mockMvc.perform(post("/api/series")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(seriesService, times(1)).createSeries(any());
    }

    @Test
    @DisplayName("시리즈 수정 테스트 (PUT /api/series/{id})")
    void updateSeriesTest() throws Exception {
        // given
        SeriesRequestDto requestDto = new SeriesRequestDto("테스트 시리즈 수정");
        doNothing().when(seriesService).updateSeries(anyLong(), any());

        // when & then
        mockMvc.perform(put("/api/series/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNoContent());

        verify(seriesService, times(1)).updateSeries(anyLong(), any());
    }

    @Test
    @DisplayName("시리즈 삭제 테스트 (DELETE /api/series/{id})")
    void deleteSeriesTest() throws Exception {
        // given
        doNothing().when(seriesService).deleteSeries(anyLong());

        // when & then
        mockMvc.perform(delete("/api/series/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(seriesService, times(1)).deleteSeries(1L);
    }
}