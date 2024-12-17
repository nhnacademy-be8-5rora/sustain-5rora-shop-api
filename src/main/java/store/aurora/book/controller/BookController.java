package store.aurora.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.BookDetailsUpdateDTO;
import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.dto.BookResponseDTO;
import store.aurora.book.dto.BookSalesInfoDTO;
import store.aurora.book.entity.Book;
import store.aurora.book.mapper.BookMapper;
import store.aurora.book.service.BookService;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody @Valid BookRequestDTO requestDTO) {
        Book savedBook = bookService.saveBookWithPublisherAndSeries(requestDTO);
        BookResponseDTO response = BookMapper.toDTO(savedBook);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{bookId}/details")
    public ResponseEntity<Book> updateBookDetails(
            @PathVariable Long bookId,
            @RequestBody BookDetailsUpdateDTO requestDTO) {
        return ResponseEntity.ok(bookService.updateBookDetails(bookId, requestDTO));
    }

    @PutMapping("/{bookId}/sales-info")
    public ResponseEntity<Book> updateBookSalesInfo(
            @PathVariable Long bookId,
            @RequestBody BookSalesInfoDTO salesInfoDTO) {
        return ResponseEntity.ok(bookService.updateBookSalesInfo(bookId, salesInfoDTO));
    }

    @PatchMapping("/{bookId}/packaging")
    public ResponseEntity<Book> updateBookPackaging(
            @PathVariable Long bookId,
            @RequestParam boolean packaging) {
        return ResponseEntity.ok(bookService.updateBookPackaging(bookId, packaging));
    }

}