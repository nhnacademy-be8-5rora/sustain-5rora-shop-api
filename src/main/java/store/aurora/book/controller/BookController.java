package store.aurora.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.book.dto.BookDetailsUpdateDTO;
import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.dto.BookSalesInfoUpdateDTO;
import store.aurora.book.dto.aladin.BookDetailDto;
import store.aurora.book.dto.aladin.BookDto;
import store.aurora.book.dto.aladin.BookRequestDtoEx;
import store.aurora.book.entity.Book;
import store.aurora.book.service.BookAuthorService;
import store.aurora.book.service.BookImageService;
import store.aurora.book.service.BookService;

import java.util.List;


@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final BookImageService bookImageService;
    private final BookAuthorService bookAuthorService;

    // 도서 검색 API
    @GetMapping("/aladin/search")
    public ResponseEntity<List<BookDto>> searchBooks(@RequestParam String query,
                                                     @RequestParam String queryType,
                                                     @RequestParam String searchTarget,
                                                     @RequestParam(defaultValue = "1") int start) {
        List<BookDto> books = bookService.searchBooks(query, queryType, searchTarget, start);
        return ResponseEntity.ok(books);
    }
    // 특정 도서 정보 제공 API
    @GetMapping("/aladin/{bookId}")
    public ResponseEntity<BookDto> getBookById(@PathVariable String bookId) {
        BookDto selectedBook = bookService.findBookDtoById(bookId);
        return ResponseEntity.ok(selectedBook);
    }

    // API 도서 등록
    @PostMapping("/aladin/register")
    public ResponseEntity<Void> registerApiBook(@ModelAttribute BookRequestDtoEx bookRequestDto) {
        bookService.saveBookFromApi(bookRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // HTTP 201 Created 응답
    }

    // 직접 도서 등록
    @PostMapping("/register/direct")
    public ResponseEntity<Void> registerDirectBook(@ModelAttribute BookRequestDtoEx bookRequestDto) {
        bookService.saveDirectBook(bookRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // HTTP 201 Created 응답
    }


    @PostMapping
    public ResponseEntity<Void> createBook(@RequestBody @Valid BookRequestDTO requestDTO) {
        bookService.saveBook(requestDTO);
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
    public ResponseEntity<BookDetailsDto> getBookDetails(@PathVariable Long bookId) {
        BookDetailsDto bookDetails = bookService.getBookDetails(bookId);
        return ResponseEntity.ok(bookDetails);
    }
}