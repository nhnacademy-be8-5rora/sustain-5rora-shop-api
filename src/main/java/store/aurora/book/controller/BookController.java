package store.aurora.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.book.dto.BookDetailsUpdateDTO;
import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.dto.BookSalesInfoUpdateDTO;
import store.aurora.book.dto.aladin.BookDto;
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
    public ResponseEntity<Void> registerApiBook(@ModelAttribute BookDto bookDto,
                                                @RequestParam(value = "additionalImages", required = false) List<MultipartFile> additionalImages
    ) {
        bookService.saveBookFromApi(bookDto, additionalImages);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 직접 도서 등록
    @PostMapping(value = "/direct/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registerDirectBook(@ModelAttribute BookDto bookDto,
                                                   @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
                                                   @RequestParam(value = "additionalImages", required = false) List<MultipartFile> additionalImages
    ) {
        bookService.saveDirectBook(bookDto, coverImage, additionalImages);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        List<BookDto> books = bookService.getAllBooks();
        books.forEach(book -> System.out.println("Book Cover: " + book.getCover()));
        return ResponseEntity.ok(books);
    }


    @GetMapping("/{bookId}")
    public ResponseEntity<BookDetailsDto> getBookDetails(@PathVariable Long bookId) {
        BookDetailsDto bookDetails = bookService.getBookDetails(bookId);
        return ResponseEntity.ok(bookDetails);
    }
}