package store.aurora.book.controller.publisher;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.publisher.PublisherRequestDto;
import store.aurora.book.dto.publisher.PublisherResponseDto;
import store.aurora.book.service.publisher.PublisherService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/publishers")
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    public ResponseEntity<Page<PublisherResponseDto>> getAllPublishers(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PublisherResponseDto> publishers = publisherService.getAllPublishers(pageable);
        return ResponseEntity.ok(publishers);
    }

    @GetMapping("/{publisher-id}")
    public ResponseEntity<PublisherResponseDto> getPublisherById(@PathVariable("publisher-id") Long id) {
        return ResponseEntity.ok(publisherService.getPublisherById(id));
    }

    @PostMapping
    public ResponseEntity<Void> createPublisher(@Valid @RequestBody PublisherRequestDto requestDto) {
        publisherService.createPublisher(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 상태 코드만 반환
    }

    @PutMapping("/{publisher-id}")
    public ResponseEntity<Void> updatePublisher(@PathVariable("publisher-id") Long id, @Valid @RequestBody PublisherRequestDto requestDto) {
        publisherService.updatePublisher(id, requestDto);
        return ResponseEntity.noContent().build(); // 상태 코드만 반환
    }

    @DeleteMapping("/{publisher-id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable("publisher-id") Long id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
}