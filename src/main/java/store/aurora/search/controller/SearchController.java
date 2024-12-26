package store.aurora.search.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.aurora.search.service.SearchService;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;


    //디버그위한 요청 url : 5rora-test.com:9080/books/search?type=title&keyword=한강&orderBy=salePrice&orderDirection=desc&pageNum=1
    @GetMapping("/api/books/search")
    public ResponseEntity<Page<?>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String orderBy, // 정렬 기준
            @RequestParam(required = false) String orderDirection, // 정렬 방향 (asc 또는 desc)
            @RequestParam(required = false, defaultValue = "1") String pageNum) { // pageNum 기본값을 1로 설정

        int page = Integer.parseInt(pageNum) - 1; // pageNum은 1부터 시작하므로 1을 빼줘야 0-based 페이지로 맞춰짐
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

        //키워드 null 허용으로 하기. 카테고리때문에.
        Page<?> bookSearchResponseDTOPage = null;
        if (type != null && keyword != null) {
            switch (type) {
                case "title":
                    bookSearchResponseDTOPage = searchService.findBooksByTitleWithDetails(keyword, pageRequest);
                    break;
                case "category":
                    if(keyword.isEmpty())
                    {
                        keyword = "0";
                    }
                    bookSearchResponseDTOPage = searchService.findBooksByCategoryWithDetails(Long.valueOf(keyword), pageRequest);
                    break;
                case "author":
                    bookSearchResponseDTOPage = searchService.findBooksByAuthorNameWithDetails(keyword, pageRequest);
                    break;
                default:
                    break;
            }
        }

        if (bookSearchResponseDTOPage == null) {
            return ResponseEntity.noContent().build();  // 결과가 없으면 204 No Content 반환
        }

        // PageImpl을 사용하여 반환
        List<?> content = bookSearchResponseDTOPage.getContent();
        long totalElements = bookSearchResponseDTOPage.getTotalElements();
        Page<?> pageResult = new PageImpl<>(content, pageRequest, totalElements);

        return ResponseEntity.ok().body(pageResult);  // 결과가 있으면 200 OK 반환
    }



}
