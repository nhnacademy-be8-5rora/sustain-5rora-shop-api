package store.aurora.book.service.series.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.aurora.book.dto.series.SeriesRequestDto;
import store.aurora.book.dto.series.SeriesResponseDto;
import store.aurora.book.entity.Series;
import store.aurora.book.exception.series.SeriesAlreadyExistsException;
import store.aurora.book.exception.series.SeriesLinkedToBooksException;
import store.aurora.book.exception.series.SeriesNotFoundException;
import store.aurora.book.repository.book.BookRepository;
import store.aurora.book.repository.series.SeriesRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeriesServiceImplTest {

    @InjectMocks
    private SeriesServiceImpl seriesService;

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private BookRepository bookRepository;

    private Series testSeries;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testSeries = new Series( "테스트 시리즈");
    }

    @Test
    @DisplayName("모든 시리즈 목록을 페이지네이션으로 조회")
    void testGetAllSeries() {
        // Given
        Pageable pageable = mock(Pageable.class);
        List<Series> seriesList = List.of(testSeries);
        when(seriesRepository.findAllByOrderById(pageable)).thenReturn(new PageImpl<>(seriesList));

        // When
        Page<SeriesResponseDto> result = seriesService.getAllSeries(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("테스트 시리즈");
    }

    @Test
    @DisplayName("ID로 시리즈 조회")
    void testGetSeriesById() {
        // Given
        when(seriesRepository.findById(1L)).thenReturn(Optional.of(testSeries));

        // When
        SeriesResponseDto response = seriesService.getSeriesById(1L);

        // Then
        assertThat(response.getName()).isEqualTo("테스트 시리즈");
    }

    @Test
    @DisplayName("존재하지 않는 시리즈 조회 시 예외 발생")
    void testGetSeriesById_NotFound() {
        // Given
        when(seriesRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> seriesService.getSeriesById(1L))
                .isInstanceOf(SeriesNotFoundException.class);
    }

    @Test
    @DisplayName("새로운 시리즈 추가")
    void testCreateSeries() {
        // Given
        SeriesRequestDto request = new SeriesRequestDto("새 시리즈");
        when(seriesRepository.findByName(request.getName())).thenReturn(Optional.empty());

        // When
        seriesService.createSeries(request);

        // Then
        verify(seriesRepository, times(1)).save(any(Series.class));
    }

    @Test
    @DisplayName("중복된 시리즈 추가 시 예외 발생")
    void testCreateSeries_Duplicate() {
        // Given
        SeriesRequestDto request = new SeriesRequestDto("테스트 시리즈");
        when(seriesRepository.findByName(request.getName())).thenReturn(Optional.of(testSeries));

        // When & Then
        assertThatThrownBy(() -> seriesService.createSeries(request))
                .isInstanceOf(SeriesAlreadyExistsException.class);
    }

    @Test
    @DisplayName("시리즈 정보 수정")
    void testUpdateSeries() {
        // Given
        SeriesRequestDto updateRequest = new SeriesRequestDto("수정된 시리즈");
        when(seriesRepository.findById(1L)).thenReturn(Optional.of(testSeries));
        when(seriesRepository.findByName(updateRequest.getName())).thenReturn(Optional.empty());

        // When
        seriesService.updateSeries(1L, updateRequest);

        // Then
        assertThat(testSeries.getName()).isEqualTo("수정된 시리즈");
    }

    @Test
    @DisplayName("시리즈 삭제")
    void testDeleteSeries() {
        // Given
        when(seriesRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.existsBySeriesId(1L)).thenReturn(false);

        // When
        seriesService.deleteSeries(1L);

        // Then
        verify(seriesRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 시리즈 삭제 시 예외 발생")
    void testDeleteSeries_NotFound() {
        // Given
        when(seriesRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> seriesService.deleteSeries(1L))
                .isInstanceOf(SeriesNotFoundException.class);
    }

    @Test
    @DisplayName("연결된 책이 있는 시리즈 삭제 시 예외 발생")
    void testDeleteSeries_LinkedToBooks() {
        // Given
        when(seriesRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.existsBySeriesId(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> seriesService.deleteSeries(1L))
                .isInstanceOf(SeriesLinkedToBooksException.class);
    }

    @Test
    @DisplayName("존재하는 시리즈 조회 시 새로운 객체를 생성하지 않고 반환해야 한다")
    void testGetOrCreateSeries_Existing() {
        // Given
        when(seriesRepository.findByName("테스트 시리즈")).thenReturn(Optional.of(testSeries));

        // When
        Series result = seriesService.getOrCreateSeries("테스트 시리즈");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 시리즈");
        verify(seriesRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 시리즈 조회 시 새로 생성하고 저장해야 한다")
    void testGetOrCreateSeries_New() {
        // Given
        when(seriesRepository.findByName("새 시리즈")).thenReturn(Optional.empty());
        when(seriesRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Series result = seriesService.getOrCreateSeries("새 시리즈");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("새 시리즈");
        verify(seriesRepository, times(1)).save(any(Series.class));
    }
}