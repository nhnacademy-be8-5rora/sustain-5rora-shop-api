package store.aurora.book.controller.tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.book.dto.tag.TagResponseDto;
import store.aurora.book.service.tag.TagService;


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

    @GetMapping
    public ResponseEntity<Page<TagResponseDto>> getAllTags(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TagResponseDto> tags = tagService.getTags(pageable);
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{tag-id}")
    public ResponseEntity<TagResponseDto> getTagById(@PathVariable("tag-id") Long id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @PutMapping("/{tag-id}")
    public ResponseEntity<TagResponseDto> updateTag(@PathVariable("tag-id") Long id, @Valid @RequestBody TagRequestDto requestDto) {
        return ResponseEntity.ok(tagService.updateTag(id, requestDto));
    }

    @DeleteMapping("/{tag-id}")
    public ResponseEntity<Void> deleteTag(@PathVariable("tag-id") Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

}
