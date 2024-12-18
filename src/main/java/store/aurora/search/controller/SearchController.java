package store.aurora.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.aurora.search.dto.BookSearchResponseDTO;
import store.aurora.search.service.SearchService;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    // 디버깅을 위한 요청 URL 예시:
    // http://localhost:8083/api/books/search?type=title&keyword=한강&pageNum=1
    @GetMapping("/api/books/search")
    public ResponseEntity<Page<BookSearchResponseDTO>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false, defaultValue = "1") String pageNum) {

        if (Objects.isNull(type) || type.isBlank() || Objects.isNull(keyword) || keyword.isBlank()) {
            return ResponseEntity.badRequest().body(Page.empty());
        }

        // 페이지 정보를 생성
        int page = Integer.parseInt(pageNum) - 1; // pageNum은 1부터 시작하므로 0-based로 맞춤
        Pageable pageable = PageRequest.of(page, 8); // 페이지 크기는 8로 고정

        Page<BookSearchResponseDTO> resultPage;

        // 검색 유형(type)에 따라 SearchService의 메서드 호출
        switch (type) {
            case "title":
                resultPage = searchService.findBooksByTitleWithDetails(keyword, pageable);
                break;
            case "category":
                resultPage = searchService.findBooksByCategoryWithDetails(Long.valueOf(keyword), pageable);
                break;
            case "author":
                resultPage = searchService.findBooksByAuthorNameWithDetails(keyword, pageable);
                break;
            default:
                return ResponseEntity.badRequest().body(Page.empty());
        }

        // 결과 반환
        if (resultPage.isEmpty()) {
            return ResponseEntity.noContent().build(); // 결과가 없을 경우 204 No Content
        }
        return ResponseEntity.ok(resultPage); // 결과가 있을 경우 200 OK
    }
}
