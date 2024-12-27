package store.aurora.book.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.book.dto.BookDetailsUpdateDTO;
import store.aurora.book.dto.BookInfoDTO;
import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.dto.ReviewDto;
import store.aurora.book.dto.BookSalesInfoUpdateDTO;
import store.aurora.book.dto.aladin.AladinApiResponse;
import store.aurora.book.dto.aladin.BookDetailDto;
import store.aurora.book.dto.aladin.BookDto;
import store.aurora.book.dto.aladin.BookRequestDtoEx;
import store.aurora.book.dto.tag.BookTagRequestDto;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.Publisher;
import store.aurora.book.entity.Series;
import store.aurora.book.exception.book.ISBNAlreadyExistsException;
import store.aurora.book.exception.book.NotFoundBookException;
import store.aurora.book.entity.*;
import store.aurora.book.exception.BookNotFoundException;
import store.aurora.book.exception.category.CategoryLimitException;
import store.aurora.book.mapper.BookMapper;
import store.aurora.book.repository.BookImageRepository;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.repository.PublisherRepository;
import store.aurora.book.repository.SeriesRepository;
import store.aurora.book.service.*;
import store.aurora.book.service.category.BookCategoryService;
import store.aurora.book.service.tag.TagService;
import store.aurora.book.util.AladinBookClient;
//import store.aurora.file.FileStorageService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final BookAuthorService bookAuthorService;
    private final List<BookDto> cachedBooks = new ArrayList<>();
    private final AladinBookClient aladinBookClient;
    private final ObjectMapper objectMapper;
    private final BookImageService bookImageService;
    private final PublisherRepository publisherRepository;
    private final SeriesRepository seriesRepository;



    @Value("${aladin.api.ttb-key}")
    private String ttbKey;

    @Override
    public List<BookDto> searchBooks(String query, String queryType, String searchTarget, int start) {
        try {
            // Aladin API 호출
            String response = aladinBookClient.searchBooks(
                    ttbKey, query, queryType, 50, start, searchTarget, "js", "20131101"
            );
            // API 응답 매핑
            AladinApiResponse apiResponse = objectMapper.readValue(response, AladinApiResponse.class);
            // 캐싱에 저장

            List<BookDto> books = apiResponse.getItems();
            cachedBooks.clear();
            cachedBooks.addAll(books);

            return books;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse API response", e);
        }
    }
    @Override
    public void saveBookFromApi(BookRequestDtoEx bookRequestDto) {
        Book book = convertToEntity(bookRequestDto);
        bookRepository.save(book);
        bookAuthorService.parseAndSaveBookAuthors(book, bookRequestDto.getAuthor());
        bookImageService.processApiImages(book, bookRequestDto.getCover(), bookRequestDto.getUploadedImages());
    }
    @Override
    public void saveDirectBook(BookRequestDtoEx bookRequestDto) {
        Book book = convertToEntity(bookRequestDto);
        // 책 저장
        bookRepository.save(book);
        // 작가 정보 저장
        bookAuthorService.parseAndSaveBookAuthors(book, bookRequestDto.getAuthor());
        // 이미지 처리
        bookImageService.processAndSaveImages(book, bookRequestDto.getUploadedImages());

    }

    @Override
    public BookDto findBookDtoById(String isbn13) {
        return cachedBooks.stream()
                .filter(book -> book.getIsbn13().equals(isbn13))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
    }


    private Book convertToEntity(BookRequestDtoEx bookDto) {

        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setExplanation(bookDto.getDescription());
        book.setIsbn(bookDto.getIsbn13());
        book.setSalePrice(bookDto.getPriceSales());
        book.setRegularPrice(bookDto.getPriceStandard());
        if (bookDto.getPubDate() != null && !bookDto.getPubDate().isBlank()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            book.setPublishDate(LocalDate.parse(bookDto.getPubDate(), formatter));
        }
        book.setStock(bookDto.getStock());
        book.setSale(bookDto.isForSale());
        book.setPackaging(bookDto.isPackaged());

        Publisher publisher = publisherRepository.findByName(bookDto.getPublisher())
                .orElseGet(() -> publisherRepository.save(new Publisher(bookDto.getPublisher())));
        book.setPublisher(publisher);

        if (bookDto.getSeriesInfo() != null && !bookDto.getSeriesInfo().getSeriesName().isBlank()) {
            Series series = seriesRepository.findByName(bookDto.getSeriesInfo().getSeriesName())
                    .orElseGet(() -> seriesRepository.save(new Series(bookDto.getSeriesInfo().getSeriesName())));
            book.setSeries(series);
        }

        return book;
    }


    @Transactional
    public void saveBook(BookRequestDTO requestDTO) {
        if (bookRepository.existsByIsbn(requestDTO.getIsbn())) {
            throw new ISBNAlreadyExistsException(requestDTO.getIsbn());
        }

        Publisher publisher = publisherService.findOrCreatePublisher(requestDTO.getPublisherName());

        Series series = null;
        if (requestDTO.getSeriesName() != null) {
            series = seriesService.findOrCreateSeries(requestDTO.getSeriesName());
        }

        Book book = BookMapper.toEntity(requestDTO);
        book.setPublisher(publisher);
        book.setSeries(series);
        Book savedBook = bookRepository.save(book);
        bookAuthorService.parseAndSaveBookAuthors(savedBook, requestDTO.getAuthor());

        if (!CollectionUtils.isEmpty(requestDTO.getCategoryIds())) {
            bookCategoryService.addCategoriesToBook(savedBook.getId(), requestDTO.getCategoryIds());
        }else {
            throw new CategoryLimitException();

        }
        if (!CollectionUtils.isEmpty(requestDTO.getTagIds())) {
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
        book.setSale(detailsDTO.isSale());

        // 출판사 업데이트
        if (StringUtils.hasText(detailsDTO.getPublisherName())) {
            Publisher publisher = publisherService.findOrCreatePublisher(detailsDTO.getPublisherName());
            book.setPublisher(publisher);
        }

        // 시리즈 업데이트
        if (StringUtils.hasText(detailsDTO.getSeriesName())) {
            Series series = seriesService.findOrCreateSeries(detailsDTO.getSeriesName());
            book.setSeries(series);
        } else {
            book.setSeries(null); // 시리즈 이름이 없으면 null로 설정
        }
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
        double avg = 0.0;
        if(Objects.nonNull(bookDetailsDto.getReviews())) {
            for (ReviewDto reviewDto : bookDetailsDto.getReviews()) {
                int reviewRating = reviewDto.getReviewRating();
                sum += reviewRating;
            }
            avg = Math.round((sum / bookDetailsDto.getReviews().size() * 10) / 10.0);
        }


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