package store.aurora.book.controller.publisher;

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
import store.aurora.book.dto.publisher.PublisherRequestDto;
import store.aurora.book.dto.publisher.PublisherResponseDto;
import store.aurora.book.service.publisher.PublisherService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/publishers")
@Tag(name = "Publisher API", description = "출판사 관리 API")
public class PublisherController {

    private final PublisherService publisherService;

    @Operation(summary = "모든 출판사 조회", description = "페이지네이션을 적용하여 모든 출판사를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 출판사 목록을 반환함",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PublisherResponseDto.class)))
    @GetMapping
    public ResponseEntity<Page<PublisherResponseDto>> getAllPublishers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PublisherResponseDto> publishers = publisherService.getAllPublishers(pageable);
        return ResponseEntity.ok(publishers);
    }

    @Operation(summary = "출판사 상세 조회", description = "ID를 기반으로 출판사의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "출판사 정보 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PublisherResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "출판사를 찾을 수 없음 (PublisherNotFoundException)")
    @GetMapping("/{publisher-id}")
    public ResponseEntity<PublisherResponseDto> getPublisherById(@PathVariable("publisher-id") Long id) {
        return ResponseEntity.ok(publisherService.getPublisherById(id));
    }

    @Operation(summary = "출판사 생성", description = "새로운 출판사를 추가합니다.")
    @ApiResponse(responseCode = "201", description = "출판사 생성 성공")
    @ApiResponse(responseCode = "409", description = "이미 존재하는 출판사 (PublisherAlreadyExistsException)")
    @PostMapping
    public ResponseEntity<Void> createPublisher(@Valid @RequestBody PublisherRequestDto requestDto) {
        publisherService.createPublisher(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "출판사 정보 수정", description = "ID를 기반으로 출판사 정보를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "출판사 정보 수정 성공")
    @ApiResponse(responseCode = "404", description = "출판사를 찾을 수 없음 (PublisherNotFoundException)")
    @ApiResponse(responseCode = "409", description = "이미 존재하는 출판사 이름 (PublisherAlreadyExistsException)")
    @PutMapping("/{publisher-id}")
    public ResponseEntity<Void> updatePublisher(
            @PathVariable("publisher-id") Long id,
            @Valid @RequestBody PublisherRequestDto requestDto) {
        publisherService.updatePublisher(id, requestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "출판사 삭제", description = "ID를 기반으로 출판사 정보를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "출판사 삭제 성공")
    @ApiResponse(responseCode = "404", description = "출판사를 찾을 수 없음 (PublisherNotFoundException)")
    @ApiResponse(responseCode = "409", description = "출판사와 연결된 책이 있어 삭제 불가 (PublisherLinkedToBooksException)")
    @DeleteMapping("/{publisher-id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable("publisher-id") Long id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
}