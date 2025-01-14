package store.aurora.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.Book;
import store.aurora.review.entity.Review;
import store.aurora.user.entity.User;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBook(Book book);
    List<Review> findByUser(User user);

    boolean existsByBookIdAndUserId(Long bookId, String userId);
    int countByBookId(Long bookId);  // bookId에 해당하는 Review 의 개수 반환
    List<Review> findByBookId(Long bookId);

}
