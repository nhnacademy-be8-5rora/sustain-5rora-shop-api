package store.aurora.book.controller.author;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.author.AuthorRequestDto;
import store.aurora.book.dto.author.AuthorResponseDto;
import store.aurora.book.service.author.AuthorService;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<Page<AuthorResponseDto>> getAllAuthors(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(authorService.getAllAuthors(pageable));
    }

    @GetMapping("/{author-id}")
    public ResponseEntity<AuthorResponseDto> getAuthorById(@PathVariable("author-id") Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @PostMapping
    public ResponseEntity<Void> createAuthor(@Valid @RequestBody AuthorRequestDto requestDto) {
        authorService.createAuthor(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{author-id}")
    public ResponseEntity<Void> updateAuthor(@PathVariable("author-id") Long id, @Valid @RequestBody AuthorRequestDto requestDto) {
        authorService.updateAuthor(id, requestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{author-id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable("author-id") Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}