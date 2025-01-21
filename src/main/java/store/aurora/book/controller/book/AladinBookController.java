package store.aurora.book.controller.book;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.aladin.AladinBookRequestDto;
import store.aurora.book.service.book.AladinBookService;

import java.util.List;

@RestController
@RequestMapping("/api/aladin")
@RequiredArgsConstructor
public class AladinBookController {

    private final AladinBookService aladinBookService;
    //알라딘 api 도서 검색
    @GetMapping("/search")
    public ResponseEntity<List<AladinBookRequestDto>> searchBooks(@RequestParam String query,
                                                           @RequestParam(defaultValue = "Keyword") String queryType,
                                                           @RequestParam(defaultValue = "Book") String searchTarget,
                                                           @RequestParam(defaultValue = "1") int start) {
        List<AladinBookRequestDto> books = aladinBookService.searchBooks(query, queryType, searchTarget, start);
        return ResponseEntity.ok(books);
    }
    // 알라딘 api 특정 도서 검색
    @GetMapping("/{isbn}")
    public ResponseEntity<AladinBookRequestDto> getBookDetailsByIsbn(@PathVariable String isbn) {
        AladinBookRequestDto book = aladinBookService.getBookDetailsByIsbn(isbn);
        return ResponseEntity.ok(book);
    }
}