package store.aurora.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.aladin.AladinBookDto;
import store.aurora.book.dto.aladin.BookRequestDto;
import store.aurora.book.service.aladin.AladinBookService;

import java.util.List;

@RestController
@RequestMapping("/api/aladin")
@RequiredArgsConstructor
public class AladinBookController {

    private final AladinBookService aladinBookService;

    @GetMapping("/search")
    public ResponseEntity<List<AladinBookDto>> searchBooks(@RequestParam String query,
                                                           @RequestParam(defaultValue = "Keyword") String queryType,
                                                           @RequestParam(defaultValue = "Book") String searchTarget,
                                                           @RequestParam(defaultValue = "1") int start) {
        List<AladinBookDto> books = aladinBookService.searchBooks(query, queryType, searchTarget, start);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{isbn13}")
    public ResponseEntity<AladinBookDto> getBookDetailsByIsbn(@PathVariable String isbn13) {
        AladinBookDto book = aladinBookService.getBookDetailsByIsbn(isbn13);
        return ResponseEntity.ok(book);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerApiBook(@Valid @ModelAttribute BookRequestDto bookDto,
                                                @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages) {
        aladinBookService.saveBookFromApi(bookDto, additionalImages);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}