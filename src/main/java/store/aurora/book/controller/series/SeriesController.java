package store.aurora.book.controller.series;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.series.SeriesRequestDto;
import store.aurora.book.dto.series.SeriesResponseDto;
import store.aurora.book.service.series.SeriesService;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
@Tag(name = "Series API", description = "도서 시리즈 관리 API")
public class SeriesController {

    private final SeriesService seriesService;

    @Operation(summary = "모든 시리즈 조회", description = "페이지네이션을 적용하여 모든 시리즈를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 시리즈 목록을 반환함",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SeriesResponseDto.class)))
    @GetMapping
    public ResponseEntity<Page<SeriesResponseDto>> getAllSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SeriesResponseDto> series = seriesService.getAllSeries(pageable);
        return ResponseEntity.ok(series);
    }

    @Operation(summary = "시리즈 상세 조회", description = "ID를 기반으로 특정 시리즈의 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "시리즈 정보 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SeriesResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "시리즈를 찾을 수 없음 (SeriesNotFoundException)")
    @GetMapping("/{series-id}")
    public ResponseEntity<SeriesResponseDto> getSeriesById(@PathVariable("series-id") Long id) {
        SeriesResponseDto series = seriesService.getSeriesById(id);
        return ResponseEntity.ok(series);
    }

    @Operation(summary = "시리즈 생성", description = "새로운 시리즈를 추가합니다.")
    @ApiResponse(responseCode = "201", description = "시리즈 생성 성공")
    @ApiResponse(responseCode = "409", description = "이미 존재하는 시리즈 (SeriesAlreadyExistsException)")
    @PostMapping
    public ResponseEntity<Void> createSeries(@Valid @RequestBody SeriesRequestDto requestDto) {
        seriesService.createSeries(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "시리즈 정보 수정", description = "ID를 기반으로 시리즈 정보를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "시리즈 정보 수정 성공")
    @ApiResponse(responseCode = "404", description = "시리즈를 찾을 수 없음 (SeriesNotFoundException)")
    @ApiResponse(responseCode = "409", description = "이미 존재하는 시리즈 (SeriesAlreadyExistsException)")
    @PutMapping("/{series-id}")
    public ResponseEntity<Void> updateSeries(
            @PathVariable("series-id") Long id,
            @Valid @RequestBody SeriesRequestDto requestDto) {
        seriesService.updateSeries(id, requestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "시리즈 삭제", description = "ID를 기반으로 시리즈 정보를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "시리즈 삭제 성공")
    @ApiResponse(responseCode = "404", description = "시리즈를 찾을 수 없음 (SeriesNotFoundException)")
    @ApiResponse(responseCode = "409", description = "시리즈와 연결된 책이 있어 삭제 불가 (SeriesLinkedToBooksException)")
    @DeleteMapping("/{series-id}")
    public ResponseEntity<Void> deleteSeries(@PathVariable("series-id") Long id) {
        seriesService.deleteSeries(id);
        return ResponseEntity.noContent().build();
    }
}