package store.aurora.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.book.dto.BookDetailsUpdateDTO;
import store.aurora.book.dto.BookInfoDTO;
import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.dto.ReviewDto;
import store.aurora.book.dto.BookSalesInfoUpdateDTO;
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
import store.aurora.book.service.BookService;
import store.aurora.book.service.PublisherService;
import store.aurora.book.service.SeriesService;
import store.aurora.book.service.category.BookCategoryService;
import store.aurora.book.service.tag.TagService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final PublisherService publisherService;
    private final SeriesService seriesService;
    private final BookCategoryService bookCategoryService;
    private final TagService tagService;
    private final BookImageRepository bookImageRepository;

    @Transactional
    public void saveBookWithPublisherAndSeries(BookRequestDTO requestDTO) {
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

    }
    @Transactional
    public void updateBookDetails(Long bookId, BookDetailsUpdateDTO detailsDTO) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundBookException(bookId));

        // 출판사 및 시리즈 정보 업데이트
        Publisher publisher = publisherService.findOrCreatePublisher(detailsDTO.getPublisherName());
        Series series = seriesService.findOrCreateSeries(detailsDTO.getSeriesName());

        // 중복 ISBN 체크
        Optional<Book> existingBook = bookRepository.findByIsbn(detailsDTO.getIsbn());
        if (existingBook.isPresent() && !existingBook.get().getId().equals(bookId)) {
            throw new ISBNAlreadyExistsException(detailsDTO.getIsbn());
        }

        book.setTitle(detailsDTO.getTitle());
        book.setExplanation(detailsDTO.getExplanation());
        book.setContents(detailsDTO.getContents());
        book.setIsbn(detailsDTO.getIsbn());
        book.setPublishDate(detailsDTO.getPublishDate());
        book.setPublisher(publisher);
        book.setSeries(series);
        book.setSale(detailsDTO.isSale());

        bookRepository.save(book);
    }

    @Transactional
    public void updateBookSalesInfo(Long bookId, BookSalesInfoUpdateDTO salesInfoDTO) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundBookException(bookId));

        book.setSalePrice(salesInfoDTO.getSalePrice());
        book.setStock(salesInfoDTO.getStock());

        bookRepository.save(book);
    }

    @Transactional
    public void updateBookPackaging(Long bookId, boolean packaging) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundBookException(bookId));

        book.setPackaging(packaging);
        bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
    }

    @Transactional(readOnly = true)
    public BookDetailsDto getBookDetails(Long bookId) {

        if (!bookRepository.existsById(bookId)) {
            throw new NotFoundBookException(bookId);
        }

        BookDetailsDto bookDetailsDto = bookRepository.findBookDetailsByBookId(bookId);

        double sum = 0;
        double avg;
        for (ReviewDto reviewDto : bookDetailsDto.getReviews()) {
            int reviewRating = reviewDto.getReviewRating();
            sum += reviewRating;
        }

        avg = Math.round((sum / bookDetailsDto.getReviews().size() * 10) / 10.0);

        bookDetailsDto.setRating(avg);

        return bookDetailsDto;
    }

    public List<BookInfoDTO> getBookInfo(List<Long> bookIds) {
        List<Book> books = bookRepository.findAllById(bookIds);

        Map<Long, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getId, book -> book));

        return bookIds.stream()
                .map(bookId -> {
                    Book book = bookMap.get(bookId);
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

    @Transactional(readOnly = true)
    public void notExistThrow(Long bookId) {
        if (!bookRepository.existsById(bookId))
            throw new BookNotFoundException(bookId);
    }
}