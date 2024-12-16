package store.aurora.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.dto.BookResponseDTO;
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

    @PutMapping("/{bookId}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long bookId, @RequestBody @Valid BookRequestDTO requestDTO) {
        Book updatedBook = bookService.updateBook(bookId, requestDTO);
        return ResponseEntity.ok(BookMapper.toDTO(updatedBook));

    }

}