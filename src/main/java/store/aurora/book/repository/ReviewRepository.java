package store.aurora.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {}
