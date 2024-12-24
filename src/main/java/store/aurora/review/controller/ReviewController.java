package store.aurora.review.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.review.dto.ReviewRequest;
import store.aurora.review.entity.Review;
import store.aurora.review.service.ReviewService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@Validated
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // 리뷰 등록
    @PostMapping
    public ResponseEntity<String> createReview(@RequestBody @Valid ReviewRequest request,
                                               @RequestParam Long bookId,
                                               @RequestParam String userId) {
        try {
//            Review review = reviewService.saveReview(content, rating, files);
            reviewService.saveReview(request, bookId, userId);
            return ResponseEntity.status(201).body("Upload OK"); //body(review);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage()); //.body(null);
        }
    }

    // 도서 ID로 리뷰 조회
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Review>> getReviewsByBookId(@PathVariable Long bookId) {
        List<Review> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }

    // 사용자 ID로 리뷰 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUserId(@PathVariable String userId) {
        List<Review> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }
}
