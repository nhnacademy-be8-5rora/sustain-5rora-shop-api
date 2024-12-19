//package store.aurora.book.controller;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import store.aurora.file.FileStorageService;
//
//import java.io.IOException;
//
//@RestController
//@RequestMapping("/books/images")
//public class BookImageController {
//
//    private final FileStorageService fileStorageService;
//
//    public BookImageController(FileStorageService fileStorageService) {
//        this.fileStorageService = fileStorageService;
//    }
//
//    @PostMapping("/upload/{bookId}")
//    public ResponseEntity<String> uploadBookImage(@PathVariable Long bookId,
//                                                  @RequestParam("file") MultipartFile file) {
//        try {
//            String directory = "Books/" + bookId;
//            String filePath = fileStorageService.uploadFile(file, directory);
//            return ResponseEntity.ok("File uploaded to: " + filePath);
//        } catch (IOException e) {
//            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
//        }
//    }
//
//    @DeleteMapping("/delete")
//    public ResponseEntity<String> deleteBookImage(@RequestParam("filePath") String filePath) {
//        try {
//            fileStorageService.deleteFile(filePath);
//            return ResponseEntity.ok("File deleted: " + filePath);
//        } catch (IOException e) {
//            return ResponseEntity.status(500).body("Failed to delete file: " + e.getMessage());
//        }
//    }
//}