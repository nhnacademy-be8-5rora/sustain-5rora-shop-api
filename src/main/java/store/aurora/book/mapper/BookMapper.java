package store.aurora.book.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import store.aurora.book.dto.aladin.BookDetailDto;
import store.aurora.book.dto.aladin.BookRequestDto;
import store.aurora.book.dto.aladin.BookResponseDto;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.category.BookCategory;
import store.aurora.book.entity.tag.BookTag;
import store.aurora.book.entity.tag.Tag;
import store.aurora.book.service.BookAuthorService;
import store.aurora.book.service.BookImageService;
import store.aurora.book.service.PublisherService;
import store.aurora.book.service.SeriesService;
import store.aurora.book.service.category.CategoryService;
import store.aurora.book.service.tag.TagService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookMapper {
    private final PublisherService publisherService;
    private final SeriesService seriesService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final BookAuthorService bookAuthorService;
    private final BookImageService bookImageService;

    // BookRequestDto -> Book 변환
    public Book toEntity(BookRequestDto bookDto) {
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setExplanation(bookDto.getDescription());
        book.setContents(bookDto.getContents());
        book.setIsbn(bookDto.getIsbn13());
        book.setSalePrice(bookDto.getPriceSales());
        book.setRegularPrice(bookDto.getPriceStandard());
        book.setPublishDate(bookDto.getPubDate() != null && !bookDto.getPubDate().isBlank()
                ? LocalDate.parse(bookDto.getPubDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null);
        book.setStock(bookDto.getStock());
        book.setSale(bookDto.getIsForSale());
        book.setPackaging(bookDto.getIsPackaged());

        // Publisher
        book.setPublisher(publisherService.getOrCreatePublisher(bookDto.getPublisher()));

        // Series
        if (bookDto.getSeriesInfo() != null && !bookDto.getSeriesInfo().getSeriesName().isBlank()) {
            book.setSeries(seriesService.getOrCreateSeries(bookDto.getSeriesInfo().getSeriesName()));
        }

        // Categories
        List<BookCategory> bookCategories = categoryService.createBookCategories(bookDto.getCategoryIds());
        bookCategories.forEach(book::addBookCategory);

        // Tags
        if (bookDto.getTags() != null && !bookDto.getTags().isBlank()) {
            // 태그 파싱 및 생성/조회
            List<Tag> tags = tagService.getOrCreateTagsByName(bookDto.getTags());

            // BookTag 생성 및 연결
            List<BookTag> bookTags = tagService.createBookTags(tags);
            bookTags.forEach(book::addBookTag);
        }
            return book;
    }

    // Book -> BookResponseDto 변환
    public BookResponseDto toResponseDto(Book book) {
        BookResponseDto bookDto = new BookResponseDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(bookAuthorService.getFormattedAuthors(book));
        bookDto.setCover(bookImageService.getThumbnailPath(book));
        bookDto.setDescription(book.getExplanation());
        bookDto.setIsbn13(book.getIsbn());
        bookDto.setPriceSales(book.getSalePrice());
        bookDto.setPriceStandard(book.getRegularPrice());
        bookDto.setPubDate(book.getPublishDate() != null ? book.getPublishDate().toString() : null);
        bookDto.setStock(book.getStock());
        bookDto.setIsForSale(book.isSale());
        bookDto.setIsPackaged(book.isPackaging());
        bookDto.setPublisher(book.getPublisher().getName());

        return bookDto;
    }

    // Book -> BookDetailDto 변환
    public BookDetailDto toDetailDto(Book book) {
        BookDetailDto bookDetailDto = new BookDetailDto();
        bookDetailDto.setId(book.getId());
        bookDetailDto.setTitle(book.getTitle());
        bookDetailDto.setAuthor(bookAuthorService.getFormattedAuthors(book));
        bookDetailDto.setDescription(book.getExplanation());
        bookDetailDto.setContents(book.getContents());
        bookDetailDto.setIsbn13(book.getIsbn());
        bookDetailDto.setPublisher(book.getPublisher().getName());
        bookDetailDto.setPriceStandard(book.getRegularPrice());
        bookDetailDto.setPriceSales(book.getSalePrice());
        bookDetailDto.setPubDate(book.getPublishDate() != null ? book.getPublishDate().toString() : null);
        bookDetailDto.setStock(book.getStock());
        bookDetailDto.setIsForSale(book.isSale());
        bookDetailDto.setIsPackaged(book.isPackaging());
        bookDetailDto.setCategoryIds(book.getBookCategories().stream()
                .map(category -> category.getCategory().getId())
                .collect(Collectors.toList()));
        bookDetailDto.setTags(tagService.getFormattedTags(book));
        bookDetailDto.setCover(bookImageService.getThumbnailPath(book));
        bookDetailDto.setAdditionalImages(bookImageService.getAdditionalImages(book));

        return bookDetailDto;
    }
}
