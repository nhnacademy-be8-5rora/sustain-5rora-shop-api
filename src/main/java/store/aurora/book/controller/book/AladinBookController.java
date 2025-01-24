package store.aurora.book.controller.book;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.aladin.AladinBookRequestDto;
import store.aurora.book.service.book.AladinBookService;

import java.util.List;

@RestController
@RequestMapping("/api/aladin")
@RequiredArgsConstructor
@Tag(name = "Aladin Book API", description = "알라딘 도서 검색 API")
public class AladinBookController {

    private final AladinBookService aladinBookService;

    @Operation(summary = "알라딘 API 도서 검색", description = "알라딘 API를 이용하여 도서를 검색합니다.")
    @ApiResponse(responseCode = "200", description = "도서 목록 검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AladinBookRequestDto.class)))
    @GetMapping("/search")
    public ResponseEntity<List<AladinBookRequestDto>> searchBooks(
            @Parameter(description = "검색어", example = "자바")
            @RequestParam String query,

            @Parameter(description = "검색 유형 (Keyword, Title, Author, Publisher)", example = "Keyword")
            @RequestParam(defaultValue = "Keyword") String queryType,

            @Parameter(description = "검색 대상 (Book, Foreign)", example = "Book")
            @RequestParam(defaultValue = "Book") String searchTarget,

            @Parameter(description = "검색 시작 페이지 (1부터 최대 4까지)", example = "1")
            @RequestParam(defaultValue = "1") int start) {
        List<AladinBookRequestDto> books = aladinBookService.searchBooks(query, queryType, searchTarget, start);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "알라딘 API 특정 도서 검색", description = "ISBN을 기반으로 특정 도서를 검색합니다.")
    @ApiResponse(responseCode = "200", description = "도서 정보 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AladinBookRequestDto.class)))
    @ApiResponse(responseCode = "404", description = "해당 ISBN의 도서를 찾을 수 없음")
    @GetMapping("/{isbn}")
    public ResponseEntity<AladinBookRequestDto> getBookDetailsByIsbn(@PathVariable String isbn) {
        AladinBookRequestDto book = aladinBookService.getBookDetailsByIsbn(isbn);
        return ResponseEntity.ok(book);
    }
}