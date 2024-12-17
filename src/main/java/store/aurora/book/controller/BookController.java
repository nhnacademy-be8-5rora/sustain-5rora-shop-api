package store.aurora.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.dto.BookResponseDTO;
import store.aurora.book.dto.tag.BookTagRequestDto;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.Category;
import store.aurora.book.entity.tag.Tag;
import store.aurora.book.mapper.BookMapper;
import store.aurora.book.service.BookService;
import store.aurora.book.service.tag.TagService;

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


    @GetMapping("/{bookId}")
    public ResponseEntity<BookDetailsDto> getBookDetails(@PathVariable Long bookId) {
        BookDetailsDto bookDetails = bookService.getBookDetails(bookId);
        return ResponseEntity.ok(bookDetails);
    }

}