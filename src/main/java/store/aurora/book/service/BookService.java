package store.aurora.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.dto.tag.BookTagRequestDto;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookCategory;
import store.aurora.book.entity.Category;
import store.aurora.book.entity.Publisher;
import store.aurora.book.entity.Series;
import store.aurora.book.mapper.BookMapper;
import store.aurora.book.repository.BookCategoryRepository;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.repository.CategoryRepository;
import store.aurora.book.service.tag.TagService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final PublisherService publisherService;
    private final SeriesService seriesService;
    private final BookCategoryService bookCategoryService;
    private final TagService tagService;
    // 책 등록 (출판사와, 시리즈 연결)
    @Transactional
    public Book saveBookWithPublisherAndSeries(BookRequestDTO requestDTO) {
        // 출판사 및 시리즈 처리
        Publisher publisher = publisherService.findOrCreatePublisher(requestDTO.getPublisherName());
        Series series = seriesService.findOrCreateSeries(requestDTO.getSeriesName());

        // 기존 책 중복 확인
        validateDuplicateBook(requestDTO, publisher);

        // 책 엔티티 생성
        Book book = BookMapper.toEntity(requestDTO, publisher, series);
        Book savedBook = bookRepository.save(book);

        // 카테고리 추가
        if (requestDTO.getCategoryIds() != null && !requestDTO.getCategoryIds().isEmpty()) {
            bookCategoryService.addCategoriesToBook(savedBook.getId(), requestDTO.getCategoryIds());
        }

        // 태그 추가
        if (requestDTO.getTagIds() != null && !requestDTO.getTagIds().isEmpty()) {
            for (Long tagId : requestDTO.getTagIds()) {
                BookTagRequestDto bookTagRequestDto = new BookTagRequestDto(savedBook.getId(), tagId);
                tagService.addBookTag(bookTagRequestDto);
            }
        }

        return savedBook;
    }

    private void validateDuplicateBook(BookRequestDTO requestDTO, Publisher publisher) {
        if (bookRepository.existsByTitleAndPublisherAndPublishDate(
                requestDTO.getTitle(), publisher, requestDTO.getPublishDate())) {
            throw new IllegalArgumentException("이미 등록된 책입니다.");
        }
    }
    // 카테고리
    @Transactional
    public void addCategoriesToBook(Long bookId, List<Long> categoryIds) {
        bookCategoryService.addCategoriesToBook(bookId, categoryIds);
    }

    @Transactional
    public void removeCategoriesFromBook(Long bookId, List<Long> categoryIds) {
        bookCategoryService.removeCategoriesFromBook(bookId, categoryIds);
    }

    public List<Category> getCategoriesByBookId(Long bookId) {
        return bookCategoryService.getCategoriesByBookId(bookId);
    }


//    // 태그
//    @Transactional
//    public void addTagsToBook(Long bookId, List<Long> tagIds) {
//        tagService.addBookTag(bookId, tagIds);
//    }
//
//    @Transactional
//    public void removeTagsFromBook(Long bookId, List<Long> tagIds) {
//        tagService.removeBookTag(bookId, tagIds);
//    }
}