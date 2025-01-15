package store.aurora.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.entity.Book;
import store.aurora.book.exception.BookNotFoundException;
import store.aurora.book.repository.BookRepository;
import store.aurora.file.ObjectStorageService;
import store.aurora.order.repository.OrderDetailRepository;
import store.aurora.point.service.PointHistoryService;
import store.aurora.review.dto.ReviewRequest;
import store.aurora.review.dto.ReviewResponse;
import store.aurora.review.entity.Review;
import store.aurora.review.entity.ReviewImage;
import store.aurora.review.exception.ReviewAlreadyExistsException;
import store.aurora.review.exception.ReviewNotFoundException;
import store.aurora.review.exception.UnauthorizedReviewException;
import store.aurora.review.repository.ReviewRepository;
import store.aurora.user.entity.User;
import store.aurora.user.exception.UserNotFoundException;
import store.aurora.user.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderDetailRepository orderDetailRepository;

    private final ObjectStorageService objectStorageService;
    private final PointHistoryService pointHistoryService;
    
    private static final Logger LOG = LoggerFactory.getLogger("user-logger");

    @Transactional
    // 리뷰 등록
    public void saveReview(ReviewRequest request, List<MultipartFile> files, Long bookId, String userId) throws IOException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 해당 사용자가 이 책을 주문했는지 확인
        boolean hasPurchasedBook = orderDetailRepository.existsByOrderUserIdAndBookId(userId, bookId);
        if (!hasPurchasedBook) {
            throw new UnauthorizedReviewException("이 도서를 주문하지 않아 리뷰를 작성할 수 없습니다.");
        }

        // 이미 해당 책의 리뷰를 작성한 경우
        if (reviewRepository.existsByBookIdAndUserId(bookId, userId)) {
            throw new ReviewAlreadyExistsException("리뷰를 이미 작성하셨습니다.");
        }

        Review review = new Review();
        review.setReviewRating(request.getRating());
        review.setReviewContent(request.getContent());
        review.setReviewCreateAt(LocalDateTime.now());
        review.setBook(book);
        review.setUser(user);

        List<ReviewImage> images = new ArrayList<>();
        for (MultipartFile file : files) {
            String imageUrl = objectStorageService.uploadObject(file);
            ReviewImage image = new ReviewImage();
            image.setImageFilePath(imageUrl);
            image.setReview(review);
            images.add(image);
        }

        review.setReviewImages(images);
        Review savedRiview = reviewRepository.save(review);

        try{
            pointHistoryService.earnReviewPoint(savedRiview.getUser(), !savedRiview.getReviewImages().isEmpty());
        } catch (Exception e) { // case: review가 null, empty인데 getFirst,
            // todo: 예상 가능한 에러 별 브라우저 응답 다르게 (잠깐 db 에러는 적립 재시도)
            LOG.warn("Failed to earn points: category=review, userId={}", user.getId(), e);
        }
    }

    // 리뷰 목록 조회 (도서 ID로 조회)
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByBookId(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        return reviewRepository.findByBook(book).stream()
                .map(review -> {
                    ReviewResponse response = new ReviewResponse();
                    response.setRating(review.getReviewRating());
                    response.setContent(review.getReviewContent());
                    response.setReviewCreateAt(review.getReviewCreateAt());
                    response.setBookId(review.getBook().getId());
                    response.setUserId(review.getUser().getId());

                    List<ReviewImage> reviewImages = review.getReviewImages();
                    List<String> reviewImgPaths = new ArrayList<>();
                    for(ReviewImage reviewImage : reviewImages) {
                        reviewImgPaths.add(reviewImage.getImageFilePath());
                    }
                    response.setImageFilePath(reviewImgPaths);

                    return response;
                })
                .toList();
    }

    // 리뷰 목록 조회 (사용자 ID로 조회)
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return reviewRepository.findByUser(user).stream()
                .map(review -> {
                    ReviewResponse response = new ReviewResponse();
                    response.setRating(review.getReviewRating());
                    response.setContent(review.getReviewContent());
                    response.setReviewCreateAt(review.getReviewCreateAt());
                    response.setBookId(review.getBook().getId());
                    response.setUserId(review.getUser().getId());

                    List<ReviewImage> reviewImages = review.getReviewImages();
                    List<String> reviewImgPaths = new ArrayList<>();
                    for(ReviewImage reviewImage : reviewImages) {
                        reviewImgPaths.add(reviewImage.getImageFilePath());
                    }
                    response.setImageFilePath(reviewImgPaths);

                    return response;
                })
                .toList();
    }


    // 리뷰 수정
    @Transactional
    public void updateReview(Long reviewId, ReviewRequest request, List<MultipartFile> files, Long bookId, String userId) throws IOException {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        existingReview.setReviewRating(request.getRating());
        existingReview.setReviewContent(request.getContent());
        existingReview.setReviewCreateAt(LocalDateTime.now());

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        existingReview.setBook(book);
        existingReview.setUser(user);

        // 기존 이미지 삭제
        List<ReviewImage> existingImages = new ArrayList<>(existingReview.getReviewImages());
        existingImages.clear();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String imageUrl = objectStorageService.uploadObject(file);
                    ReviewImage image = new ReviewImage();
                    image.setImageFilePath(imageUrl);
                    image.setReview(existingReview);
                    existingImages.add(image);
                }
            }
        }

        existingReview.setReviewImages(existingImages);
        reviewRepository.save(existingReview);
    }

}
