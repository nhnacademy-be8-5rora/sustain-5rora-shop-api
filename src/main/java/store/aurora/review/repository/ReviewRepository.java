package store.aurora.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.aurora.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
