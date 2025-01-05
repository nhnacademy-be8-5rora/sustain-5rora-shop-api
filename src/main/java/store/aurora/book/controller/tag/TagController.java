package store.aurora.book.controller.tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.book.dto.tag.TagResponseDto;
import store.aurora.book.service.tag.TagService;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @PostMapping
    public ResponseEntity<TagResponseDto> createTag(@Valid @RequestBody TagRequestDto requestDto) {
        TagResponseDto responseDto = tagService.createTag(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<TagResponseDto>> getAllTags(Pageable pageable) {
        Page<TagResponseDto> tags = tagService.getAllTags(pageable);
        return ResponseEntity.ok(tags);
    }
    @GetMapping("/all")
    public ResponseEntity<List<TagResponseDto>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDto> getTagById(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponseDto> updateTag(@PathVariable Long id, @Valid @RequestBody TagRequestDto requestDto) {
        return ResponseEntity.ok(tagService.updateTag(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<TagResponseDto>> searchTags(@RequestParam String keyword) {
        List<TagResponseDto> tags = tagService.searchTags(keyword);
        return ResponseEntity.ok(tags);
    }
}
