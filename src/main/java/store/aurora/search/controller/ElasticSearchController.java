package store.aurora.search.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.search.dto.BookSearchResponseDTO;
import store.aurora.search.service.ElasticSearchService;

@RestController
@RequestMapping("/api/books/search/elastic-search")
@RequiredArgsConstructor
public class ElasticSearchController {

    private final ElasticSearchService elasticSearchService;

    @Operation(summary = "책 검색 (ElasticSearch)",
            description = "ElasticSearch를 사용하여 책을 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 결과를 성공적으로 반환했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookSearchResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "검색 결과가 없습니다.",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터입니다.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<BookSearchResponseDTO>> searchBooks(
            @Parameter(description = "요청을 보낸 사용자 ID (X-USER-ID 헤더)", required = false)
            @RequestHeader(name = "X-USER-ID", required = false) String userId,

            @Parameter(description = "검색 유형 (예: 제목, 저자 등)", required = false)
            @RequestParam(required = false) String type,

            @Parameter(description = "검색 키워드", required = false)
            @RequestParam(required = false) String keyword,

            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)", required = false)
            @RequestParam(required = false, defaultValue = "0") int pageNum) {

        if (pageNum < 0) {
            return ResponseEntity.badRequest().build();
        }

        PageRequest pageRequest = PageRequest.of(pageNum, 8);
        Page<BookSearchResponseDTO> books = elasticSearchService.searchBooks(type, keyword, pageRequest, userId);

        if (books == null || books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(books);
    }
}
