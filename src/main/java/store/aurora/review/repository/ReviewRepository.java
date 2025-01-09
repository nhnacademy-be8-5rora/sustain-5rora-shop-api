package store.aurora.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.aurora.book.entity.Book;
import store.aurora.review.entity.Review;
import store.aurora.user.entity.User;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBook(Book book);
    List<Review> findByUser(User user);

    boolean existsByBookIdAndUserId(Long bookId, String userId);
}
