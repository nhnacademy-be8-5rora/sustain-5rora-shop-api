package store.aurora.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.Like;
import store.aurora.book.exception.book.NotFoundBookException;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.repository.LikeRepository;
import store.aurora.book.service.LikeService;
import store.aurora.search.dto.BookSearchEntityDTO;
import store.aurora.search.dto.BookSearchResponseDTO;
import store.aurora.user.entity.User;
import store.aurora.user.exception.UserNotFoundException;
import store.aurora.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    //엘라스틱에 반영하도록
    @Override
    public boolean pressLike(Long bookId, String userId) {
        Book findBook = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundBookException(bookId));
        User findUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        // 이미 좋아요를 눌렀는지 확인
        Like like = likeRepository.findByUserAndBook(findUser, findBook)
                .orElseGet(() -> new Like(findBook, findUser, false)); // 없으면 새로 생성

        // 이미 좋아요가 눌려있으면 토글(좋아요 취소)
        like.setLike(!like.isLike());

        // 저장
        likeRepository.save(like);
        return true; // 저장에 성공하면 true 반환
    }


    @Override
    public Like cancelLike(Long bookId, String userId) {
        // 책과 사용자가 존재하는지 확인
        Book findBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundBookException(bookId)); // 책이 없으면 예외 발생
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId)); // 사용자가 없으면 예외 발생

        // 좋아요가 눌려 있지 않으면 취소할 수 없음
        Like like = likeRepository.findByUserAndBook(findUser, findBook)
                .orElseThrow(() -> new IllegalArgumentException("좋아요를 누르지 않은 책입니다."));

        // 좋아요 취소
        like.setLike(false);
        return likeRepository.save(like);
    }


    //    @EntityGraph(attributePaths = "book") 를 이용하여 N+1 문제 해결
    @Override
    public Set<Long> getLikedBookIds(String userId, List<Long> bookIds) {
        // LIKE 테이블에서 유저가 좋아요를 누른 책들의 ID를 가져오는 쿼리
        return likeRepository.findByUserIdAndBookIdInAndIsLikeTrue(userId, bookIds)
                .stream()
                .map(like -> like.getBook().getId())  // book 객체에서 ID를 가져옴
                .collect(Collectors.toSet());
    }


}
