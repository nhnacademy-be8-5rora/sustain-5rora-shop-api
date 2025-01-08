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

    // 디버그위한 요청 url : 5rora-test.com:9080/books/search?type=title&keyword=한강&orderBy=salePrice&orderDirection=desc&pageNum=1
    @GetMapping("/api/books/search")
    public ResponseEntity<Page<BookSearchResponseDTO>> search(
            @RequestHeader(value = "X-USER-ID", required = false) String userId,  // X-USER-ID 헤더에서 userId를 받아옴
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String orderBy, // 정렬 기준
            @RequestParam(required = false) String orderDirection, // 정렬 방향 (asc 또는 desc)
            @RequestParam(required = false, defaultValue = "1") String pageNum) { // pageNum 기본값을 1로 설정


        int page;
        try {
            page = Integer.parseInt(pageNum) - 1;
            if (page < 0) {
                return ResponseEntity.badRequest().body(null); // pageNum이 1보다 작은 경우 처리
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(null); // 잘못된 pageNum 값에 대한 응답
        }
        PageRequest pageRequest;

        // orderBy가 null이면 기본값 설정
        if (orderBy == null || orderBy.isEmpty()) {
            orderBy = "id"; // 기본값을 "id"로 설정
        }
        // 정렬 방향 설정 (디폴트는 오름차순)
        if ("desc".equalsIgnoreCase(orderDirection)) {
            pageRequest = PageRequest.of(page, 8, Sort.by(Sort.Order.desc(orderBy)));
        } else {
            pageRequest = PageRequest.of(page, 8, Sort.by(Sort.Order.asc(orderBy)));
        }

        // 키워드 null 허용
        Page<BookSearchResponseDTO> bookSearchResponseDTOPage = null;
        if (type != null && keyword != null) {
            switch (type) {
                case "title":
                    bookSearchResponseDTOPage = searchService.findBooksByTitleWithDetails(userId, keyword, pageRequest);
                    break;
                case "category":
                    if(keyword.isEmpty()) {
                        keyword = "0";
                    }
                    try {
                        Long categoryId = Long.valueOf(keyword); // 카테고리 ID 변환
                        bookSearchResponseDTOPage = searchService.findBooksByCategoryWithDetails(userId, categoryId, pageRequest);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("category id는 숫자만 입력 가능합니다.");
                    }
                    break;
                case "author":
                    bookSearchResponseDTOPage = searchService.findBooksByAuthorNameWithDetails(userId, keyword, pageRequest);
                    break;
                default:
                    break;
            }
        }

        if (bookSearchResponseDTOPage == null) {
            return ResponseEntity.noContent().build();  // 결과가 없으면 204 No Content 반환
        }

        // PageImpl을 사용하여 반환
        List<BookSearchResponseDTO> content = bookSearchResponseDTOPage.getContent();
        long totalElements = bookSearchResponseDTOPage.getTotalElements();
        Page<BookSearchResponseDTO> pageResult = new PageImpl<>(content, pageRequest, totalElements);

        return ResponseEntity.ok().body(pageResult);  // 결과가 있으면 200 OK 반환
    }
}
