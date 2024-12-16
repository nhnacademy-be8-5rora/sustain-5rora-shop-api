package store.aurora.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.dto.tag.BookTagRequestDto;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.Publisher;
import store.aurora.book.entity.Series;
import store.aurora.book.exception.book.ISBNAlreadyExistsException;
import store.aurora.book.exception.book.NotFoundBookException;
import store.aurora.book.mapper.BookMapper;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.service.category.BookCategoryService;
import store.aurora.book.service.tag.TagService;

import java.util.Optional;

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
        Publisher publisher = publisherService.findOrCreatePublisher(requestDTO.getPublisherName());
        Series series = seriesService.findOrCreateSeries(requestDTO.getSeriesName());

        if (bookRepository.existsByIsbn(requestDTO.getIsbn())) {
            throw new ISBNAlreadyExistsException(requestDTO.getIsbn());
        }

        Book book = BookMapper.toEntity(requestDTO, publisher, series);
        Book savedBook = bookRepository.save(book);

        if (requestDTO.getCategoryIds() != null && !requestDTO.getCategoryIds().isEmpty()) {
            bookCategoryService.addCategoriesToBook(savedBook.getId(), requestDTO.getCategoryIds());
        }

        if (requestDTO.getTagIds() != null && !requestDTO.getTagIds().isEmpty()) {
            for (Long tagId : requestDTO.getTagIds()) {
                BookTagRequestDto bookTagRequestDto = new BookTagRequestDto(savedBook.getId(), tagId);
                tagService.addBookTag(bookTagRequestDto);
            }
        }
        return savedBook;
    }

    @Transactional
    public Book updateBook(Long bookId, BookRequestDTO requestDTO) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundBookException(bookId));

        Publisher publisher = publisherService.findOrCreatePublisher(requestDTO.getPublisherName());
        Series series = seriesService.findOrCreateSeries(requestDTO.getSeriesName());

        Optional<Book> existingBook = bookRepository.findByIsbn(requestDTO.getIsbn());
        if (existingBook.isPresent() && !existingBook.get().getId().equals(bookId)) {
            throw new ISBNAlreadyExistsException(requestDTO.getIsbn());
        }

        book.setTitle(requestDTO.getTitle());
        book.setRegularPrice(requestDTO.getRegularPrice());
        book.setSalePrice(requestDTO.getSalePrice());
        book.setPackaging(requestDTO.isPackaging());
        book.setStock(requestDTO.getStock());
        book.setExplanation(requestDTO.getExplanation());
        book.setContents(requestDTO.getContents());
        book.setIsbn(requestDTO.getIsbn());
        book.setPublishDate(requestDTO.getPublishDate());
        book.setSale(requestDTO.isSale());
        book.setPublisher(publisher);
        book.setSeries(series);

        return bookRepository.save(book);
    }
}