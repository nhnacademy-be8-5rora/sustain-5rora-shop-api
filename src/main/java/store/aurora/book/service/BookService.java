package store.aurora.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookCategory;
import store.aurora.book.entity.Category;
import store.aurora.book.entity.Publisher;
import store.aurora.book.entity.Series;
import store.aurora.book.exception.BookNotFoundException;
import store.aurora.book.mapper.BookMapper;
import store.aurora.book.repository.BookCategoryRepository;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final PublisherService publisherService;
    private final SeriesService seriesService;
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

    @Transactional
    public Book saveBookWithPublisherAndSeries(BookRequestDTO requestDTO) {
        // 출판사 및 시리즈 처리
        Publisher publisher = publisherService.findOrCreatePublisher(requestDTO.getPublisherName());
        Series series = seriesService.findOrCreateSeries(requestDTO.getSeriesName());

        // 기존 책 중복 확인
        Optional<Book> existingBook = bookRepository.findByTitleAndPublisherAndPublishDate(
                requestDTO.getTitle(), publisher, requestDTO.getPublishDate()
        );
        if (existingBook.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 책입니다.");
        }

        // 카테고리 처리
        if (requestDTO.getCategoryIds() == null || requestDTO.getCategoryIds().isEmpty()) {
            throw new IllegalArgumentException("카테고리는 최소 하나 이상 선택해야 합니다.");
        }

        List<Category> categories = categoryRepository.findAllById(requestDTO.getCategoryIds());
        if (categories.size() != requestDTO.getCategoryIds().size()) {
            throw new IllegalArgumentException("유효하지 않은 카테고리 ID가 포함되어 있습니다.");
        }

        // 책 엔티티 생성
        Book book = BookMapper.toEntity(requestDTO, publisher, series, categories);

        return bookRepository.save(book);
    }

    @Transactional
    public void addCategoriesToBook(Long bookId, List<Long> categoryIds) {
        Book book = getBookById(bookId);

        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID입니다: " + categoryId));

            // 중복된 카테고리 추가 방지
            boolean alreadyExists = book.getBookCategories()
                    .stream()
                    .anyMatch(bookCategory -> bookCategory.getCategory().getId().equals(categoryId));
            if (!alreadyExists) {
                BookCategory bookCategory = new BookCategory();
                bookCategory.setBook(book);
                bookCategory.setCategory(category);
                book.getBookCategories().add(bookCategory);
            }
        }
    }

    @Transactional
    public void removeCategoriesFromBook(Long bookId, List<Long> categoryIds) {
        Book book = getBookById(bookId);

        // 삭제할 카테고리 제거
        book.getBookCategories().removeIf(bookCategory ->
                categoryIds.contains(bookCategory.getCategory().getId()));
    }

    public List<Category> getCategoriesByBookId(Long bookId) {
        Book book = getBookById(bookId);

        return book.getBookCategories()
                .stream()
                .map(BookCategory::getCategory)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
    }
}