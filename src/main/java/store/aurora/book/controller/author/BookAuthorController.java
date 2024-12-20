package store.aurora.book.controller.author;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.entity.BookAuthor;
import store.aurora.book.entity.Author;
import store.aurora.book.entity.AuthorRole;
import store.aurora.book.service.BookAuthorService;

import java.util.List;

@RestController
@RequestMapping("/api/books/{bookId}/authors")
@RequiredArgsConstructor
public class BookAuthorController {

    private final BookAuthorService bookAuthorService;

    @PostMapping
    public ResponseEntity<BookAuthor> addAuthorToBook(
            @PathVariable Long bookId,
            @RequestBody Author author,
            @RequestBody AuthorRole authorRole) {
        return ResponseEntity.ok(bookAuthorService.addAuthorToBook(bookId, author, authorRole));
    }

    @DeleteMapping("/{bookAuthorId}")
    public ResponseEntity<Void> removeAuthorFromBook(
            @PathVariable Long bookId,
            @PathVariable Long bookAuthorId) {
        bookAuthorService.removeAuthorFromBook(bookAuthorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<List<BookAuthor>> addAuthorsToBookBatch(
            @PathVariable Long bookId,
            @RequestBody List<BookAuthor> bookAuthors) {
        return ResponseEntity.ok(bookAuthorService.addAuthorsToBook(bookId, bookAuthors));
    }
}
