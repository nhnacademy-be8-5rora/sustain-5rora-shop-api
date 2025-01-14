package store.aurora.search.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.search.dto.BookSearchResponseDTO;
import store.aurora.search.service.ElasticSearchService;


@RestController
@RequestMapping("/api/books/search/elasticSearch")
@RequiredArgsConstructor
public class ElasticSearchController {

    private final ElasticSearchService elasticSearchService;

    @GetMapping
    public ResponseEntity<Page<BookSearchResponseDTO>> getBooks(@RequestHeader(name ="X-USER-ID",required = false) String userId,
                                           @RequestParam(required = false) String type,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false,defaultValue = "1") String pageNum) {
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(pageNum),8);
        Page<BookSearchResponseDTO> books = elasticSearchService.searchBooks(type,keyword,pageRequest,userId);
        return ResponseEntity.ok(books);
    }
}
