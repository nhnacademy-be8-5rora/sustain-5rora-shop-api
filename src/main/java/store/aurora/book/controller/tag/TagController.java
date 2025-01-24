package store.aurora.book.controller.tag;

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
import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.book.dto.tag.TagResponseDto;
import store.aurora.book.service.tag.TagService;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(name = "Tag API", description = "책 태그 관리 API")
public class TagController {
    private final TagService tagService;

    @Operation(summary = "태그 생성", description = "새로운 태그를 추가합니다.")
    @ApiResponse(responseCode = "201", description = "태그 생성 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagResponseDto.class)))
    @ApiResponse(responseCode = "409", description = "이미 존재하는 태그 (TagAlreadyExistException)")
    @PostMapping
    public ResponseEntity<TagResponseDto> createTag(@Valid @RequestBody TagRequestDto requestDto) {
        TagResponseDto responseDto = tagService.createTag(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "모든 태그 조회", description = "페이지네이션을 적용하여 모든 태그를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 태그 목록을 반환함",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagResponseDto.class)))
    @GetMapping
    public ResponseEntity<Page<TagResponseDto>> getAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TagResponseDto> tags = tagService.getTags(pageable);
        return ResponseEntity.ok(tags);
    }

    @Operation(summary = "태그 상세 조회", description = "ID를 기반으로 특정 태그의 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "태그 정보 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음 (TagNotFoundException)")
    @GetMapping("/{tag-id}")
    public ResponseEntity<TagResponseDto> getTagById(@PathVariable("tag-id") Long id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @Operation(summary = "태그 수정", description = "ID를 기반으로 태그 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "태그 정보 수정 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음 (TagNotFoundException)")
    @ApiResponse(responseCode = "409", description = "이미 존재하는 태그 (TagAlreadyExistException)")
    @PutMapping("/{tag-id}")
    public ResponseEntity<TagResponseDto> updateTag(@PathVariable("tag-id") Long id, @Valid @RequestBody TagRequestDto requestDto) {
        return ResponseEntity.ok(tagService.updateTag(id, requestDto));
    }

    @Operation(summary = "태그 삭제", description = "ID를 기반으로 태그 정보를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "태그 삭제 성공")
    @ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음 (TagNotFoundException)")
    @DeleteMapping("/{tag-id}")
    public ResponseEntity<Void> deleteTag(@PathVariable("tag-id") Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}