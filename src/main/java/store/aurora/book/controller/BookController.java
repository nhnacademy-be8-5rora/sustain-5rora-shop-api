package store.aurora.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.BookDetailsDto;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.BookDetailsUpdateDTO;
import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.dto.BookSalesInfoUpdateDTO;
import store.aurora.book.entity.Book;
import store.aurora.book.service.BookService;
//import store.aurora.file.FileStorageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
//    private final FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<Void> createBook(
            @RequestBody @Valid BookRequestDTO requestDTO) throws IOException {

        // 파일 업로드 처리
//        List<String> uploadedPaths = new ArrayList<>();
//        if (requestDTO.getFiles() != null) {
//            for (MultipartFile file : requestDTO.getFiles()) {
//                String uploadedPath = fileStorageService.uploadFile(file,"Books/");
//                uploadedPaths.add(uploadedPath);
//            }
//        }
//
//        // 업로드된 파일 경로를 DTO에 설정
//        requestDTO.setImagePaths(uploadedPaths);

        bookService.saveBook(requestDTO);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{bookId}/details")
    public ResponseEntity<Void> updateBookDetails(
            @PathVariable Long bookId,
            @RequestBody BookDetailsUpdateDTO requestDTO) {
        bookService.updateBookDetails(bookId, requestDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{bookId}/sales-info")
    public ResponseEntity<Void> updateBookSalesInfo(
            @PathVariable Long bookId,
            @RequestBody BookSalesInfoUpdateDTO salesInfoDTO) {
        bookService.updateBookSalesInfo(bookId, salesInfoDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{bookId}/packaging")
    public ResponseEntity<Void> updateBookPackaging(
            @PathVariable Long bookId,
            @RequestParam boolean packaging) {
        bookService.updateBookPackaging(bookId, packaging);
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/{bookId}/images")
//    public ResponseEntity<Void> addBookImages(
//            @PathVariable Long bookId,
//            @RequestPart("files") List<MultipartFile> files) throws IOException {
//        bookService.addBookImages(bookId, files);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//
//    @PatchMapping("/{bookId}/images/{imageId}/thumbnail")
//    public ResponseEntity<Void> updateThumbnail(
//            @PathVariable Long bookId,
//            @PathVariable Long imageId) {
//        bookService.updateThumbnail(bookId, imageId);
//        return ResponseEntity.ok().build();
//    }
//
//    @DeleteMapping("/{bookId}/images/{imageId}")
//    public ResponseEntity<Void> deleteBookImage(
//            @PathVariable Long bookId,
//            @PathVariable Long imageId) throws IOException {
//        bookService.deleteBookImage(bookId, imageId);
//        return ResponseEntity.ok().build();
//    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDetailsDto> getBookDetails(@PathVariable Long bookId) {
        BookDetailsDto bookDetails = bookService.getBookDetails(bookId);
        return ResponseEntity.ok(bookDetails);
    }
}