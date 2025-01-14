package store.aurora.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import store.aurora.review.dto.ReviewResponse;
import store.aurora.review.entity.Review;
import store.aurora.review.service.ReviewService;
import store.aurora.user.entity.User;

import java.io.IOException;
import java.util.ArrayList;
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
    @Operation(summary = "리뷰 등록", description = "리뷰를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Review.class)))
    @ApiResponse(responseCode = "403", description = "도서를 주문하지 않음")
    @ApiResponse(responseCode = "409", description = "리뷰를 이미 작성함")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    public ResponseEntity<String> createReview(@RequestBody @Valid ReviewRequest request,
                                               @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                               @RequestParam Long bookId,
                                               @RequestParam String userId) {
        if (files == null) { files = new ArrayList<>(); }
        try {
            reviewService.saveReview(request, files, bookId, userId);
            return ResponseEntity.status(201).body("Upload OK"); //body(review);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage()); //.body(null);
        }
    }

    // 도서 ID로 리뷰 조회
    @GetMapping("/book/{bookId}")
    @Operation(summary = "도서 ID로 리뷰 리스트 조회", description = "해당 bookId의 도서의 리뷰 리스트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Review.class)))
    @ApiResponse(responseCode = "404", description = "해당 도서를 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    public ResponseEntity<List<ReviewResponse>> getReviewsByBookId(@PathVariable Long bookId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }

    // 사용자 ID로 리뷰 조회
    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자 ID로 리뷰 리스트 조회", description = "해당 userId의 사용자가 작성한 리뷰 리스트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Review.class)))
    @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUserId(@PathVariable String userId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    // 리뷰 상세 조회
    @GetMapping("/{reviewId}")
    @Operation(summary = "리뷰 상세 조회", description = "해당 reviewId의 리뷰 상세내용을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Review.class)))
    @ApiResponse(responseCode = "404", description = "해당 리뷰를 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    public ReviewResponse getReviewById(@PathVariable Long reviewId) {
        return reviewService.getReviewById(reviewId);
    }


    // 리뷰 수정
    @PutMapping("/{reviewId}/edit")
    @Operation(summary = "리뷰 수정", description = "리뷰를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Review.class)))
    @ApiResponse(responseCode = "404", description = "해당 (리뷰/도서/유저)가 존재하지 않음")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    public ResponseEntity<String> updateReview(@PathVariable Long reviewId,
                                               @RequestBody @Valid ReviewRequest request,
                                               @RequestParam(required = false) List<MultipartFile> files,
//                                               @RequestParam Long bookId,
                                               @RequestParam String userId) {
        if (files == null) { files = new ArrayList<>(); }
        try {
            reviewService.updateReview(reviewId, request, files, userId);
            return ResponseEntity.status(200).body("Review updated successfully");
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).body("Update failed: " + e.getMessage());
        }
    }
}
