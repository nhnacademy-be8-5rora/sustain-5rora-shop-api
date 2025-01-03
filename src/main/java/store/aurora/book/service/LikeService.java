package store.aurora.book.service;


import store.aurora.book.entity.Like;

public interface LikeService {
    boolean pressLike(Long bookId, String userId);

    Like cancelLike(Long bookId, String userId);

    boolean isLiked(String userId, Long bookId);
}
