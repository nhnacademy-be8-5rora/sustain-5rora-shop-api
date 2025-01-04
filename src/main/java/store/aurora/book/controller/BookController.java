package store.aurora.book.controller;

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
import store.aurora.book.dto.BookDetailsUpdateDTO;
import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.dto.BookSalesInfoUpdateDTO;
import store.aurora.book.dto.aladin.BookDetailDto;
import store.aurora.book.dto.aladin.BookRequestDto;
import store.aurora.book.dto.aladin.BookResponseDto;
import store.aurora.book.page.CustomPage;
import store.aurora.book.service.BookAuthorService;
import store.aurora.book.service.BookImageService;
import store.aurora.book.service.BookService;
import store.aurora.search.dto.BookSearchResponseDTO;

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
    public ResponseEntity<List<BookRequestDto>> searchBooks(@RequestParam String query,
                                                     @RequestParam String queryType,
                                                     @RequestParam String searchTarget,
                                                     @RequestParam(defaultValue = "1") int start) {
        List<BookRequestDto> books = bookService.searchBooks(query, queryType, searchTarget, start);
        return ResponseEntity.ok(books);
    }
    // 특정 도서 정보 제공 API
    @GetMapping("/aladin/{bookId}")
    public ResponseEntity<BookRequestDto> getBookById(@PathVariable String bookId) {
        BookRequestDto selectedBook = bookService.findBookRequestDtoById(bookId);
        return ResponseEntity.ok(selectedBook);
    }

    // API 도서 등록
    @PostMapping("/aladin/register")
    public ResponseEntity<Void> registerApiBook(@ModelAttribute BookRequestDto bookDto,
                                                @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages
    ) {
        bookService.saveBookFromApi(bookDto, additionalImages);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 직접 도서 등록
    @PostMapping(value = "/direct/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registerDirectBook(@ModelAttribute BookRequestDto bookDto,
                                                   @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
                                                   @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages
    ) {
        bookService.saveDirectBook(bookDto, coverImage, additionalImages);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<CustomPage<BookResponseDto>> getAllBooks(Pageable pageable) {
        Page<BookResponseDto> books = bookService.getAllBooks(pageable);
        CustomPage<BookResponseDto> customPage = new CustomPage<>(
                books.getContent(),
                books.getNumber(),      // 현재 페이지
                books.getTotalPages(),  // 총 페이지 수
                books.getTotalElements(), // 총 요소 개수
                books.getSize()         // 페이지 크기
        );
        return ResponseEntity.ok(customPage);
    }

    @PutMapping(value = "/{bookId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> editBook(@PathVariable Long bookId,
                                         @ModelAttribute BookRequestDto bookDto,
                                         @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
                                         @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
                                         @RequestPart(value = "deleteImages", required = false) List<Long> deleteImageIds) {
        bookService.updateBook(bookId, bookDto, coverImage, additionalImages, deleteImageIds);
        return ResponseEntity.noContent().build(); // 수정 후 응답으로 No Content 반환
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

}