package store.aurora.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.BookDetailsUpdateDTO;
import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.dto.BookSalesInfoUpdateDTO;
import store.aurora.book.service.BookService;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Void> createBook(@RequestBody @Valid BookRequestDTO requestDTO) {
        bookService.saveBookWithPublisherAndSeries(requestDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{bookId}/details")
    public ResponseEntity<Void> updateBookDetails(
            @PathVariable Long bookId,
            @RequestBody BookDetailsUpdateDTO requestDTO) {
        bookService.updateBookDetails(bookId, requestDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{bookId}/sales-info")
    public ResponseEntity<Void> updateBookSalesInfo(
            @PathVariable Long bookId,
            @RequestBody BookSalesInfoUpdateDTO salesInfoDTO) {
        bookService.updateBookSalesInfo(bookId, salesInfoDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{bookId}/packaging")
    public ResponseEntity<Void> updateBookPackaging(
            @PathVariable Long bookId,
            @RequestParam boolean packaging) {
        bookService.updateBookPackaging(bookId, packaging);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Void> getBookDetails(@PathVariable Long bookId) {
        bookService.getBookDetails(bookId);
        return ResponseEntity.ok().build();
    }
}