package store.aurora.book.controller.tag;

import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.tag.BookTagRequestDto;
import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.book.dto.tag.TagResponseDto;
import store.aurora.book.service.tag.TagService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping("/api/tags")
    public ResponseEntity<Void> createTag(@Valid @RequestBody TagRequestDto requestDto) {
        tagService.createTag(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @DeleteMapping("/api/tags/{tagId}")
    public ResponseEntity<Void> removeTag(@PathVariable Long tagId) {
        tagService.removeTag(tagId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/book-tag/")
    public ResponseEntity<Void> addBookTag(@RequestBody BookTagRequestDto requestDto) {
        tagService.addBookTag(requestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("api/book-tag/{bookTagId}")
    public ResponseEntity<Void> removeBookTag(@PathVariable Long bookTagId) {
        tagService.removeBookTag(bookTagId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<TagResponseDto>> getAllTags() {
        List<TagResponseDto> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }
}
