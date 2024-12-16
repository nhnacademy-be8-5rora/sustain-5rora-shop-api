package store.aurora.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.BookInfoDTO;
import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.entity.*;
import store.aurora.book.exception.BookNotFoundException;
import store.aurora.book.mapper.BookMapper;
import store.aurora.book.repository.BookCategoryRepository;
import store.aurora.book.repository.BookImageRepository;
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
    private final CategoryRepository categoryRepository;
    private final BookCategoryService bookCategoryService;
    private final TagService tagService;
    private final BookImageRepository bookImageRepository;

    // 책 등록 (출판사와, 시리즈 연결)
    @Transactional
    public Book saveBookWithPublisherAndSeries(BookRequestDTO requestDTO) {
        // 출판사 및 시리즈 처리
        Publisher publisher = publisherService.findOrCreatePublisher(requestDTO.getPublisherName());
        Series series = seriesService.findOrCreateSeries(requestDTO.getSeriesName());

        // 기존 책 중복 확인
        validateDuplicateBook(requestDTO, publisher);

        // 카테고리 처리
        List<Category> categories = fetchValidCategories(requestDTO.getCategoryIds());

        // 책 엔티티 생성
        Book book = BookMapper.toEntity(requestDTO, publisher, series, categories);

        return bookRepository.save(book);
    }

    private void validateDuplicateBook(BookRequestDTO requestDTO, Publisher publisher) {
        Optional<Book> existingBook = bookRepository.findByTitleAndPublisherAndPublishDate(
                requestDTO.getTitle(), publisher, requestDTO.getPublishDate()
        );
        if (existingBook.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 책입니다.");
        }
    }

    private List<Category> fetchValidCategories(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new IllegalArgumentException("카테고리는 최소 하나 이상 선택해야 합니다.");
        }

        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new IllegalArgumentException("유효하지 않은 카테고리 ID가 포함되어 있습니다.");
        }
        return categories;
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

    @Transactional(readOnly = true)
    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
    }

    public List<BookInfoDTO> getBookInfo(List<Long> bookIds) {
        List<Book> books = bookRepository.findAllById(bookIds);

        return books.stream()
                .map(book -> {
                    BookInfoDTO bookInfoDTO = new BookInfoDTO();
                    bookInfoDTO.setTitle(book.getTitle());
                    bookInfoDTO.setRegularPrice(book.getRegularPrice());
                    bookInfoDTO.setSalePrice(book.getSalePrice());
                    bookInfoDTO.setStock(book.getStock());
                    bookInfoDTO.setSale(book.isSale());

                    List<BookImage> bookImages = bookImageRepository.findByBook(book);
                    if (!bookImages.isEmpty()) {
                        bookInfoDTO.setFilePath(bookImages.get(0).getFilePath());
                    }

                    return bookInfoDTO;
                })
                .toList();
    }
}