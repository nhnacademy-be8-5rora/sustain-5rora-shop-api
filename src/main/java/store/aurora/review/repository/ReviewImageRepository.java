package store.aurora.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.review.entity.Review;
import store.aurora.review.entity.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
}
