package store.aurora.search.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.aurora.search.dto.BookSearchResponseDTO;
import store.aurora.search.service.SearchService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private static final Logger USER_LOG = LoggerFactory.getLogger("user-logger");

    @Operation(summary = "책 조회", description = "데이터베이스에서 책을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "책 조회에 성공했습니다."),
            @ApiResponse(responseCode = "204", description = "책이 존재하지 않습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookSearchResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/api/books/search")
    public ResponseEntity<Page<BookSearchResponseDTO>> search(
            @Parameter(description = "사용자 ID (옵션)", example = "user123")
            @RequestHeader(value = "X-USER-ID", required = false) String userId,

            @Parameter(description = "검색 키워드 (옵션) ", example = "Java")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "검색 타입 (예: title, author, category, tag)", example = "title")
            @RequestParam(required = false) String type,

            @Parameter(description = "정렬 기준 (예: title, view, like, publishdate, reviewrating, saleprice )", example = "title")
            @RequestParam(required = false) String orderBy,

            @Parameter(description = "정렬 방향 (asc 또는 desc)", example = "desc")
            @RequestParam(required = false) String orderDirection,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0", required = false)
            @RequestParam(required = false, defaultValue = "0") String pageNum) {

        int page = validateAndParsePageNum(pageNum);
        if (page < 0) {
            return ResponseEntity.noContent().build();
        }

        PageRequest pageRequest = createPageRequest(page, orderBy, orderDirection);

        try {
            Page<BookSearchResponseDTO> bookSearchResponseDTOPage = searchService.findBooksByKeywordWithDetails(userId, type, keyword, pageRequest);
            if (bookSearchResponseDTOPage == null || bookSearchResponseDTOPage.isEmpty()) {
                USER_LOG.info("조회 결과가 존재하지 않습니다.");
                return ResponseEntity.noContent().build();
            }
            // PageImpl을 사용하여 반환
            List<BookSearchResponseDTO> content = bookSearchResponseDTOPage.getContent();
            long totalElements = bookSearchResponseDTOPage.getTotalElements();
            Page<BookSearchResponseDTO> pageResult = new PageImpl<>(content, pageRequest, totalElements);

            return ResponseEntity.ok(pageResult);
        } catch (IllegalArgumentException e) {
            USER_LOG.error(e.getMessage(), e);
            return ResponseEntity.status(500).build(); // 500 Internal Server Error for unexpected errors
        }
    }


    private int validateAndParsePageNum(String pageNum) {
        try {
            int page = Integer.parseInt(pageNum);
            return Math.max(page, 0);
        } catch (NumberFormatException e) {
            return -1; // 잘못된 pageNum 값 처리
        }
    }

    private PageRequest createPageRequest(int page, String orderBy, String orderDirection) {
        if (orderBy == null || orderBy.isEmpty()) {
            orderBy = "id";
        }
        Sort.Order order = "desc".equalsIgnoreCase(orderDirection)
                ? Sort.Order.desc(orderBy)
                : Sort.Order.asc(orderBy);
        return PageRequest.of(page, 8, Sort.by(order));
    }
}
