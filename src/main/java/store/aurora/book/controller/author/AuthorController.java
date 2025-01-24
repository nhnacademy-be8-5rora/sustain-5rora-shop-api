package store.aurora.book.controller.author;

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
import store.aurora.book.dto.author.AuthorRequestDto;
import store.aurora.book.dto.author.AuthorResponseDto;

import store.aurora.book.service.author.AuthorService;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Tag(name = "Author API", description = "작가 관리 API")  // API 그룹 태그 추가
public class AuthorController {

    private final AuthorService authorService;

    @Operation(summary = "모든 작가 조회", description = "페이지네이션을 적용하여 모든 작가 리스트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 작가 목록을 반환함", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorResponseDto.class)))
    @GetMapping
    public ResponseEntity<Page<AuthorResponseDto>> getAllAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(authorService.getAllAuthors(pageable));
    }


    @Operation(summary = "작가 상세 조회", description = "ID를 기반으로 작가의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "작가 정보 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "작가를 찾을 수 없음 (AuthorNotFoundException)")
    @GetMapping("/{author-id}")
    public ResponseEntity<AuthorResponseDto> getAuthorById(@PathVariable("author-id") Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @Operation(summary = "작가 생성", description = "새로운 작가 정보를 추가합니다.")
    @ApiResponse(responseCode = "201", description = "작가 생성 성공")
    @ApiResponse(responseCode = "409", description = "이미 존재하는 작가 (AuthorAlreadyExistsException)")
    @PostMapping
    public ResponseEntity<Void> createAuthor(@Valid @RequestBody AuthorRequestDto requestDto) {
        authorService.createAuthor(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "작가 정보 수정", description = "ID를 기반으로 작가 정보를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "작가 정보 수정 성공")
    @ApiResponse(responseCode = "404", description = "작가를 찾을 수 없음 (AuthorNotFoundException)")
    @ApiResponse(responseCode = "409", description = "이미 존재하는 작가 이름 (AuthorAlreadyExistsException)")
    @PutMapping("/{author-id}")
    public ResponseEntity<Void> updateAuthor(
            @PathVariable("author-id") Long id,
            @Valid @RequestBody AuthorRequestDto requestDto) {
        authorService.updateAuthor(id, requestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "작가 삭제", description = "ID를 기반으로 작가 정보를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "작가 삭제 성공")
    @ApiResponse(responseCode = "404", description = "작가를 찾을 수 없음 (AuthorNotFoundException)")
    @ApiResponse(responseCode = "409", description = "작가와 연결된 책이 있어 삭제 불가 (AuthorLinkedToBooksException)")
    @DeleteMapping("/{author-id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable("author-id") Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}