package store.aurora.book.controller.book;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.aladin.AladinBookRequestDto;
import store.aurora.book.service.aladin.AladinBookService;

import java.util.List;

@RestController
@RequestMapping("/api/aladin")
@RequiredArgsConstructor
public class AladinBookController {

    private final AladinBookService aladinBookService;

    @GetMapping("/search")
    public ResponseEntity<List<AladinBookRequestDto>> searchBooks(@RequestParam String query,
                                                           @RequestParam(defaultValue = "Keyword") String queryType,
                                                           @RequestParam(defaultValue = "Book") String searchTarget,
                                                           @RequestParam(defaultValue = "1") int start) {
        List<AladinBookRequestDto> books = aladinBookService.searchBooks(query, queryType, searchTarget, start);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<AladinBookRequestDto> getBookDetailsByIsbn(@PathVariable String isbn) {
        AladinBookRequestDto book = aladinBookService.getBookDetailsByIsbn(isbn);
        return ResponseEntity.ok(book);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerApiBook(@Valid @ModelAttribute AladinBookRequestDto bookDto,
                                                @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages) {
        aladinBookService.saveBookFromApi(bookDto, additionalImages);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}