package store.aurora.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.Like;
import store.aurora.book.exception.book.NotFoundBookException;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.repository.LikeRepository;
import store.aurora.book.service.LikeService;
import store.aurora.user.entity.User;
import store.aurora.user.exception.NotFoundUserException;
import store.aurora.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    public Like pressLike(Long bookId, String userId) {
        Book findBook = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundBookException(bookId));
        User findUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundUserException(userId));

        Like like = new Like(null, findBook, findUser, true);

        return likeRepository.save(like);
    }

    @Override
    public Like cancelLike(Long bookId, String userId) {
        return likeRepository.deleteByBookIdAndUserId(bookId, userId);
    }
}
