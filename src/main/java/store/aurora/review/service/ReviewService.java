package store.aurora.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.file.ObjectStorageService;
import store.aurora.review.entity.Review;
import store.aurora.review.entity.ReviewImage;
import store.aurora.review.repository.ReviewRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ObjectStorageService objectStorageService;

    @Transactional
    //public Review saveReview
    public void saveReview(String content, Integer rating, List<MultipartFile> files) throws IOException {
        Review review = new Review();
        review.setReviewContent(content);
        review.setReviewRating(rating);
        // todo 유민 : book, user 설정 필수

        List<ReviewImage> images = new ArrayList<>();
        for (MultipartFile file : files) {
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
}
