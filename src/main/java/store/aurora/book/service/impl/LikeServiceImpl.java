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
    public boolean pressLike(Long bookId, String userId) {
        Book findBook = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundBookException(bookId));
        User findUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundUserException(userId));

        Like like=null;
        if(likeRepository.existsLikeByBookAndUser(findBook,findUser))
        {
            like = likeRepository.findByUserAndBook(findUser,findBook);
            like.setLike(!like.isLike());
        }

        else like = new Like( findBook, findUser, true);

        Like savedLike = likeRepository.save(like);  // 저장된 Like 엔티티 반환
        return savedLike != null;  // 저장된 엔티티가 null이 아니면 성공
    }

    @Override
    public Like cancelLike(Long bookId, String userId) {
        return likeRepository.deleteByBookIdAndUserId(bookId, userId);
    }

    @Override
    // 사용자와 책을 기준으로 좋아요 여부 확인
    public boolean isLiked(String userId, Long bookId) {
        Like like = likeRepository.findByUserIdAndBookId(userId, bookId);
        return like != null && like.isLike(); // 좋아요가 눌려있으면 true, 아니면 false
    }
}
