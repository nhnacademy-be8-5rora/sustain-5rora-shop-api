package store.aurora.book.controller.book;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.book.dto.aladin.BookDetailDto;
import store.aurora.book.dto.aladin.BookRequestDto;
import store.aurora.book.dto.aladin.BookResponseDto;

import store.aurora.book.service.book.BookService;
import store.aurora.search.dto.BookSearchResponseDTO;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    // 직접 도서 등록
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registerDirectBook(@Valid @ModelAttribute BookRequestDto bookDto,
                                                   @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
                                                   @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages
    ) {
        bookService.saveBook(bookDto, coverImage, additionalImages);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<Page<BookResponseDto>> getAllBooks(Pageable pageable) {
        Page<BookResponseDto> books = bookService.getBooksByActive(true,pageable);
        return ResponseEntity.ok(books);
    }
    @GetMapping("/deactivate")
    public ResponseEntity<Page<BookResponseDto>> getDeactivateBooks(Pageable pageable) {
        Page<BookResponseDto> books = bookService.getBooksByActive(false,pageable);
        return ResponseEntity.ok(books);
    }

    @PostMapping("/{bookId}/deactivate")
    public ResponseEntity<Void> deactivateBook(@PathVariable Long bookId) {
        bookService.updateBookActivation(bookId, false);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{bookId}/activate")
    public ResponseEntity<Void> activateBook(@PathVariable Long bookId) {
        bookService.updateBookActivation(bookId, true);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{bookId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> editBook(@PathVariable Long bookId,
                                         @Valid @ModelAttribute BookRequestDto bookDto,
                                         @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
                                         @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
                                         @RequestParam(value = "deleteImages", required = false) List<Long> deleteImageIds) {
        bookService.updateBook(bookId, bookDto, coverImage, additionalImages, deleteImageIds);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookId}/edit")
    public ResponseEntity<BookDetailDto> getBookDetailsForAdmin(@PathVariable Long bookId) {
        BookDetailDto bookDetails = bookService.getBookDetailsForAdmin(bookId);
        return ResponseEntity.ok(bookDetails);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDetailsDto> getBookDetails(@PathVariable Long bookId) {
        BookDetailsDto bookDetails = bookService.getBookDetails(bookId);
        return ResponseEntity.ok(bookDetails);
    }

    //유저가 좋아요 누른 책 리스트 반환
    @GetMapping("/likes")
    public ResponseEntity<Page<BookSearchResponseDTO>> getBooksByLike(@RequestHeader(value = "X-USER-ID") String userId,
                                                                      @RequestParam Long pageNum) {
        // 페이지 번호를 0부터 시작하는 방식으로 변환 (pageNum - 1)
        PageRequest pageable = PageRequest.of(pageNum.intValue() - 1, 8);  // 크기 8로 설정

        // 좋아요 상태에 따른 책 목록을 가져오는 서비스 호출
        Page<BookSearchResponseDTO> books = bookService.getBooksByLike(userId, pageable);

        // 책 목록을 ResponseEntity로 반환
        return ResponseEntity.ok(books);  // 좋아요 상태에 맞는 책 목록 반환
    }

    @GetMapping("/most")
    public ResponseEntity<BookSearchResponseDTO> getBooksByMost() {
        Optional<BookSearchResponseDTO> book = bookService.findMostSeller();

        // 책이 있으면 200 OK와 책 정보 반환
        if (book.isPresent()) {
            return ResponseEntity.ok(book.get());
        }

        // 책이 없으면 200 OK와 함께 '책을 찾을 수 없음' 메시지 반환
        BookSearchResponseDTO notFoundResponse = new BookSearchResponseDTO();
        notFoundResponse.setTitle("책을 찾을 수 없습니다.");
        return ResponseEntity.ok(notFoundResponse);  // 책이 없으면 응답 본문에 "책을 찾을 수 없습니다." 메시지 포함
    }


}