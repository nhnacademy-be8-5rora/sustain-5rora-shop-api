package store.aurora.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.dto.BookResponseDTO;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.Category;
import store.aurora.book.mapper.BookMapper;
import store.aurora.book.service.BookService;

import java.util.List;

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

//    @PutMapping("/{id}")
//    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long id, @RequestBody @Valid BookRequestDTO requestDTO) {
//        Book updatedBook = bookService.updateBook(id, requestDTO);
//        BookResponseDTO response = BookMapper.toDTO(updatedBook);
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/{bookId}/categories")
    public ResponseEntity<Void> addCategoriesToBook(@PathVariable Long bookId, @RequestBody List<Long> categoryIds) {
        bookService.addCategoriesToBook(bookId, categoryIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{bookId}/categories")
    public ResponseEntity<Void> removeCategoriesFromBook(@PathVariable Long bookId, @RequestBody List<Long> categoryIds) {
        bookService.removeCategoriesFromBook(bookId, categoryIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{bookId}/categories")
    public ResponseEntity<List<Category>> getBookCategories(@PathVariable Long bookId) {
        List<Category> categories = bookService.getCategoriesByBookId(bookId);
        return ResponseEntity.ok(categories);
    }

}