package store.aurora.search.controller;


import lombok.RequiredArgsConstructor;
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

    @GetMapping("/api/books/search")
    public ResponseEntity<Page<BookSearchResponseDTO>> search(
            @RequestHeader(value = "X-USER-ID", required = false) String userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String orderDirection,
            @RequestParam(required = false, defaultValue = "1") String pageNum) {

        int page = validateAndParsePageNum(pageNum);
        if (page < 0) {
            return ResponseEntity.badRequest().body(null);
        }

        PageRequest pageRequest = createPageRequest(page, orderBy, orderDirection);

        try {
            Page<BookSearchResponseDTO> bookSearchResponseDTOPage = handleSearchByType(userId, keyword, type, pageRequest);
            if (bookSearchResponseDTOPage == null || bookSearchResponseDTOPage.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            // PageImpl을 사용하여 반환
            List<BookSearchResponseDTO> content = bookSearchResponseDTOPage.getContent();
            long totalElements = bookSearchResponseDTOPage.getTotalElements();
            Page<BookSearchResponseDTO> pageResult = new PageImpl<>(content, pageRequest, totalElements);

            return ResponseEntity.ok(pageResult);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    private int validateAndParsePageNum(String pageNum) {
        try {
            int page = Integer.parseInt(pageNum) - 1;
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

    private Page<BookSearchResponseDTO> handleSearchByType(String userId, String keyword, String type, PageRequest pageRequest) {
        if (type == null || keyword == null) {
            return null;
        }

        return switch (type) {
            case "title" -> searchService.findBooksByTitleWithDetails(userId, keyword, pageRequest);
            case "category" -> handleCategorySearch(userId, keyword, pageRequest);
            case "author" -> searchService.findBooksByAuthorNameWithDetails(userId, keyword, pageRequest);
            default -> null;
        };
    }

    private Page<BookSearchResponseDTO> handleCategorySearch(String userId, String keyword, PageRequest pageRequest) {
        if (keyword.isEmpty()) {
            keyword = "0";
        }
        try {
            Long categoryId = Long.valueOf(keyword);
            return searchService.findBooksByCategoryWithDetails(userId, categoryId, pageRequest);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Category ID는 숫자만 입력 가능합니다.");
        }
    }
}
