package store.aurora.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.entity.Book;
import store.aurora.book.exception.BookNotFoundException;
import store.aurora.book.repository.BookRepository;
import store.aurora.file.ObjectStorageService;
import store.aurora.review.dto.ReviewRequest;
import store.aurora.review.entity.Review;
import store.aurora.review.entity.ReviewImage;
import store.aurora.review.exception.ReviewAlreadyExistsException;
import store.aurora.review.repository.ReviewRepository;
import store.aurora.user.entity.User;
import store.aurora.user.exception.NotFoundUserException;
import store.aurora.user.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ObjectStorageService objectStorageService;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    //public Review saveReview
    public void saveReview(ReviewRequest request, Long bookId, String userId) throws IOException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(userId));

        // 이미 해당 책의 리뷰를 작성한 경우
        if (reviewRepository.existsByBookAndUser(book, user)) {
            throw new ReviewAlreadyExistsException("리뷰를 이미 작성하셨습니다.");
        }

        Review review = new Review();
        review.setReviewRating(request.getRating());
        review.setReviewContent(request.getContent());
        review.setReviewCreateAt(LocalDateTime.now());
        review.setBook(book);
        review.setUser(user);

        List<ReviewImage> images = new ArrayList<>();
        for (MultipartFile file : request.getFiles()) {
            String objectName = objectStorageService.generateUniqueFileName(file.getOriginalFilename());
            String url = objectStorageService.uploadObject("aurora", objectName, file);// todo
            ReviewImage image = new ReviewImage();
//            image.setImageFilePath(storageUrl + "/your_container_name/" + objectName);
            image.setImageFilePath(url);
            image.setReview(review);
            images.add(image);
        }
        review.setReviewImages(images);
        reviewRepository.save(review);
//        return reviewRepository.save(review);
    }

    // 리뷰 목록 조회 (도서 ID로 조회)
    public List<Review> getReviewsByBookId(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        return reviewRepository.findByBook(book);
    }

    // 리뷰 목록 조회 (사용자 ID로 조회)
    public List<Review> getReviewsByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(userId));
        return reviewRepository.findByUser(user);
    }


}
