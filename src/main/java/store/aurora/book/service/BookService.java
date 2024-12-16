package store.aurora.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.BookInfoDTO;
import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.dto.BookSalesInfoDTO;
import store.aurora.book.dto.tag.BookTagRequestDto;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.Publisher;
import store.aurora.book.entity.Series;
import store.aurora.book.exception.book.ISBNAlreadyExistsException;
import store.aurora.book.exception.book.NotFoundBookException;
import store.aurora.book.entity.*;
import store.aurora.book.exception.BookNotFoundException;
import store.aurora.book.mapper.BookMapper;
import store.aurora.book.repository.BookImageRepository;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.service.category.BookCategoryService;
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
    private final BookImageRepository bookImageRepository;

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
    public Book updateBookDetails(Long bookId, BookRequestDTO requestDTO) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundBookException(bookId));

        book.setTitle(requestDTO.getTitle());
        book.setIsbn(requestDTO.getIsbn());
        book.setPublishDate(requestDTO.getPublishDate());
        book.setExplanation(requestDTO.getExplanation());
        book.setContents(requestDTO.getContents());
        book.setPublisher(publisherService.findOrCreatePublisher(requestDTO.getPublisherName()));
        book.setSeries(seriesService.findOrCreateSeries(requestDTO.getSeriesName()));

        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBookSalesInfo(Long bookId, BookSalesInfoDTO salesInfoDTO) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundBookException(bookId));

        book.setSale(salesInfoDTO.isSale());
        book.setSalePrice(salesInfoDTO.getSalePrice());
        book.setStock(salesInfoDTO.getStock());
        book.setPackaging(salesInfoDTO.isPackaging());

        return bookRepository.save(book);
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