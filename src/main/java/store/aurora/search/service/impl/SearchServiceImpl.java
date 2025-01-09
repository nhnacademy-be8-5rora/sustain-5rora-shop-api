package store.aurora.search.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.repository.BookRepository;

import store.aurora.book.service.LikeService;
import store.aurora.search.dto.BookSearchEntityDTO;
import store.aurora.search.dto.BookSearchResponseDTO;
import store.aurora.search.service.SearchService;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static store.aurora.utils.ValidationUtils.*;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final BookRepository bookRepository;

    private final LikeService likeService;

    @Override
    public Page<BookSearchResponseDTO> findBooksByTitleWithDetails(String userId, String title, Pageable pageable) {
        if (Objects.isNull(title)) {
            return emptyPage(pageable);
        }

        // EntityDTO를 가져오는 메소드 호출
        Page<BookSearchEntityDTO> bookSearchEntityDTOPage = bookRepository.findBooksByTitleWithDetails(title, pageable);

        // BookSearchEntityDTO -> BookSearchResponseDTO로 변환
        Page<BookSearchResponseDTO> bookSearchResponseDTOPage = bookSearchEntityDTOPage.map(BookSearchResponseDTO::new);

        // userId가 null이 아니면 좋아요 상태를 확인
        if (userId != null) {
            // 모든 책들의 id를 모아서 한 번에 좋아요 여부를 조회
            List<Long> bookIds = bookSearchResponseDTOPage.getContent().stream()
                    .map(BookSearchResponseDTO::getId)
                    .toList();

            // 유저가 좋아요를 눌렀던 책 목록을 한 번에 조회
            Set<Long> likedBookIds = likeService.getLikedBookIds(userId, bookIds);

            // 각 책에 대해 좋아요 여부를 설정
            for (BookSearchResponseDTO book : bookSearchResponseDTOPage.getContent()) {
                boolean isLiked = likedBookIds.contains(book.getId());
                book.setLiked(isLiked); // 좋아요 상태를 DTO에 추가
            }
        }

        return bookSearchResponseDTOPage;
    }

    @Override
    public Page<BookSearchResponseDTO> findBooksByAuthorNameWithDetails(String userId, String name, Pageable pageable) {
        if (Objects.isNull(name) || name.isBlank()) {
            return emptyPage(pageable);
        }

        // Author name으로 책을 검색한 결과를 가져옵니다.
        Page<BookSearchEntityDTO> bookSearchEntityDTOPage = bookRepository.findBooksByAuthorNameWithDetails(name, pageable);

        // BookSearchEntityDTO -> BookSearchResponseDTO로 변환
        Page<BookSearchResponseDTO> bookSearchResponseDTOPage = bookSearchEntityDTOPage.map(BookSearchResponseDTO::new);

        // userId가 null이 아니면 좋아요 상태를 확인
        if (userId != null) {
            // 모든 책들의 id를 모아서 한 번에 좋아요 여부를 조회
            List<Long> bookIds = bookSearchResponseDTOPage.getContent().stream()
                    .map(BookSearchResponseDTO::getId)
                    .toList();

            // 유저가 좋아요를 눌렀던 책 목록을 한 번에 조회
            Set<Long> likedBookIds = likeService.getLikedBookIds(userId, bookIds);

            // 각 책에 대해 좋아요 여부를 설정
            for (BookSearchResponseDTO book : bookSearchResponseDTOPage.getContent()) {
                boolean isLiked = likedBookIds.contains(book.getId());
                book.setLiked(isLiked); // 좋아요 상태를 DTO에 추가
            }
        }

        return bookSearchResponseDTOPage;
    }

    @Override
    public Page<BookSearchResponseDTO> findBooksByCategoryWithDetails(String userId, Long categoryId, Pageable pageable) {
        if (Objects.isNull(categoryId)) {
            return emptyPage(pageable);
        }

        Page<BookSearchEntityDTO> bookSearchEntityDTOPage = bookRepository.findBooksByCategoryWithDetails(categoryId, pageable);

        // BookSearchEntityDTO -> BookSearchResponseDTO로 변환
        Page<BookSearchResponseDTO> bookSearchResponseDTOPage = bookSearchEntityDTOPage.map(BookSearchResponseDTO::new);

        // userId가 null이 아니면 좋아요 상태를 확인
        if (userId != null) {
            // 모든 책들의 id를 모아서 한 번에 좋아요 여부를 조회
            List<Long> bookIds = bookSearchResponseDTOPage.getContent().stream()
                    .map(BookSearchResponseDTO::getId)
                    .toList();


            // 유저가 좋아요를 눌렀던 책 목록을 한 번에 조회
            Set<Long> likedBookIds = likeService.getLikedBookIds(userId, bookIds);

            // 각 책에 대해 좋아요 여부를 설정
            for (BookSearchResponseDTO book : bookSearchResponseDTOPage.getContent()) {
                boolean isLiked = likedBookIds.contains(book.getId());
                book.setLiked(isLiked); // 좋아요 상태를 DTO에 추가
            }
        }

        return bookSearchResponseDTOPage;
    }
}
