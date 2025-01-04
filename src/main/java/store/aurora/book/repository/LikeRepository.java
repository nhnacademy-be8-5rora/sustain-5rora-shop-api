package store.aurora.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.Like;
import store.aurora.user.entity.User;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Like deleteByBookIdAndUserId(Long bookId, String userId);
    Like findByUserIdAndBookId(String userId, Long bookId);
    boolean existsLikeByBookAndUser(Book findBook,User findUser);
    Like findByUserAndBook (User user, Book book);
    List<Like> findByUserIdAndIsLikeTrue(String userId);

}

