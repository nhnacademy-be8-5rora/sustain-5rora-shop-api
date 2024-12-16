package store.aurora.search.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.repository.BookRepository;
import store.aurora.search.dto.BookSearchEntityDTO;
import store.aurora.search.dto.BookSearchResponseDTO;
import store.aurora.search.service.SearchService;

import java.util.Objects;

import static store.aurora.utils.ValidationUtils.*;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class SearchServiceImpl implements SearchService {
    @Autowired
    private BookRepository bookRepository;



    @Override
    public Page<BookSearchResponseDTO> findBooksByTitleWithDetails(String title, Pageable pageable) {
        if (Objects.isNull(title) || title.isEmpty()) {
            return emptyPage(pageable);
        }

        // EntityDTO를 가져오는 메소드 호출
        Page<BookSearchEntityDTO> bookSearchEntityDTOPage = bookRepository.findBooksByTitleWithDetails(title, pageable);

        // BookSearchEntityDTO -> BookSearchResponseDTO로 변환

        return bookSearchEntityDTOPage.map(BookSearchResponseDTO::new);
    }


    @Override
    public Page<BookSearchResponseDTO> findBooksByAuthorNameWithDetails(String name, Pageable pageable) {
        if (Objects.isNull(name) || name.isEmpty()) {
            return emptyPage(pageable);
        }

        // Author name으로 책을 검색한 결과를 가져옵니다.
        Page<BookSearchEntityDTO> bookSearchEntityDTOPage = bookRepository.findBooksByAuthorNameWithDetails(name, pageable);

        // BookSearchEntityDTO -> BookSearchResponseDTO로 변환
        return bookSearchEntityDTOPage.map(BookSearchResponseDTO::new);
    }

    @Override
    public Page<BookSearchResponseDTO> findBooksByCategoryNameWithDetails(String name, Pageable pageable) {
        if(Objects.isNull(name) || name.isEmpty()) {
            return emptyPage(pageable);
        }

        Page<BookSearchEntityDTO> bookSearchEntityDTOPage = bookRepository.findBooksByCategoryNameWithDetails(name, pageable);
        return bookSearchEntityDTOPage.map(BookSearchResponseDTO::new);
    }
}
