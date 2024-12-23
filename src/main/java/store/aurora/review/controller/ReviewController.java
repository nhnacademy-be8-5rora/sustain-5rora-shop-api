package store.aurora.review.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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

    @PostMapping
//    public ResponseEntity<Review>
    public ResponseEntity<String> createReview(
            @RequestParam(required = false) String content,
            @RequestParam @Min(1) @Max(5) Integer rating,
            @RequestParam(required = false) List<MultipartFile> files) {
        try {
//            Review review = reviewService.saveReview(content, rating, files);
            reviewService.saveReview(content, rating, files);
            return ResponseEntity.status(201).body("Upload OK"); //body(review);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage()); //.body(null);
        }
    }
}
