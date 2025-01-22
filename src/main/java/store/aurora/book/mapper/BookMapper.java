package store.aurora.book.mapper;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import store.aurora.book.dto.aladin.*;
import store.aurora.book.dto.category.CategoryDTO;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookImage;
import store.aurora.book.entity.Publisher;
import store.aurora.book.entity.tag.BookTag;
import store.aurora.book.entity.tag.Tag;
import store.aurora.book.service.author.BookAuthorService;
import store.aurora.book.service.image.BookImageService;
import store.aurora.book.service.publisher.PublisherService;
import store.aurora.book.service.series.SeriesService;
import store.aurora.book.service.category.CategoryService;
import store.aurora.book.service.tag.TagService;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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
    public Book aladinToEntity(AladinBookRequestDto bookDto) {
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setExplanation(bookDto.getDescription());
        book.setContents(!StringUtils.isBlank(bookDto.getContents()) ? bookDto.getContents() : null);
        book.setIsbn(bookDto.getValidIsbn());
        book.setSalePrice(bookDto.getPriceSales());
        book.setRegularPrice(bookDto.getPriceStandard());
        book.setPublishDate(!StringUtils.isBlank(bookDto.getPubDate())
                ? LocalDate.parse(bookDto.getPubDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null);
        book.setStock(bookDto.getStock());
        book.setSale(bookDto.isSale());
        book.setPackaging(bookDto.isPackaging());

        // Publisher 설정
        book.setPublisher(publisherService.getOrCreatePublisher(bookDto.getPublisher()));

        // Series 설정
        Optional.ofNullable(bookDto.getSeriesInfo())
                .map(AladinBookRequestDto.SeriesInfo::getSeriesName)
                .filter(StringUtils::isBlank)
                .ifPresent(seriesName -> book.setSeries(seriesService.getOrCreateSeries(seriesName)));

        addCategoriesAndTags(book, bookDto.getCategoryIds(), bookDto.getTags());

        return book;
    }

    public Book toEntity(BookRequestDto bookDto) {
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setExplanation(bookDto.getDescription());
        book.setContents(!StringUtils.isBlank(bookDto.getContents()) ? bookDto.getContents() : null);
        book.setIsbn(bookDto.getIsbn());
        book.setSalePrice(bookDto.getPriceSales());
        book.setRegularPrice(bookDto.getPriceStandard());
        book.setPublishDate(bookDto.getPubDate());
        book.setStock(bookDto.getStock());
        book.setSale(bookDto.isSale());
        book.setPackaging(bookDto.isPackaging());

        book.setPublisher(publisherService.getOrCreatePublisher(bookDto.getPublisher()));

        if (StringUtils.isBlank(bookDto.getSeriesName())) {
            book.setSeries(seriesService.getOrCreateSeries(bookDto.getSeriesName()));
        }
        addCategoriesAndTags(book, bookDto.getCategoryIds(), bookDto.getTags());

        return book;
    }


    // Book -> BookResponseDto 변환
    public BookResponseDto toResponseDto(Book book) {
        BookResponseDto bookDto = new BookResponseDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(bookAuthorService.getFormattedAuthors(book));
        bookDto.setCover(Optional.ofNullable(bookImageService.getThumbnail(book))
                .map(BookImage::getFilePath).orElse(null));
        bookDto.setDescription(book.getExplanation());
        bookDto.setIsbn13(book.getIsbn());
        bookDto.setPriceSales(book.getSalePrice());
        bookDto.setPriceStandard(book.getRegularPrice());
        bookDto.setPubDate(Optional.ofNullable(book.getPublishDate()).map(LocalDate::toString).orElse(null));
        bookDto.setStock(book.getStock());
        bookDto.setSale(book.isSale());
        bookDto.setPackaging(book.isPackaging());
        bookDto.setPublisher(Optional.ofNullable(book.getPublisher()).map(Publisher::getName).orElse("Unknown Publisher"));
        return bookDto;
    }

    // Book -> BookDetailDto 변환
    public BookDetailDto toDetailDto(Book book) {
        BookDetailDto bookDetailDto = new BookDetailDto();
        bookDetailDto.setTitle(book.getTitle());
        bookDetailDto.setAuthor(bookAuthorService.getFormattedAuthors(book));
        bookDetailDto.setDescription(book.getExplanation());
        bookDetailDto.setContents(book.getContents());
        bookDetailDto.setIsbn(book.getIsbn());
        bookDetailDto.setPublisher(book.getPublisher().getName());
        bookDetailDto.setPriceStandard(book.getRegularPrice());
        bookDetailDto.setPriceSales(book.getSalePrice());
        bookDetailDto.setPubDate(book.getPublishDate());
        bookDetailDto.setStock(book.getStock());
        bookDetailDto.setSale(book.isSale());
        bookDetailDto.setPackaging(book.isPackaging());
        bookDetailDto.setSeriesName(book.getSeries() != null ? book.getSeries().getName() : null);
        bookDetailDto.setCategories(book.getBookCategories().stream()
                .map(bookCategory -> new CategoryResponseDTO(
                        bookCategory.getCategory().getId(),
                        bookCategory.getCategory().getName()
                ))
                .toList());
        bookDetailDto.setTags(tagService.getFormattedTags(book));
        // Cover 이미지 처리
        BookImage thumbnailImage = bookImageService.getThumbnail(book);
        if (thumbnailImage != null) {
            bookDetailDto.setCover(new ImageDetail(thumbnailImage.getId(), thumbnailImage.getFilePath()));
        }

        // Additional Images 처리
        List<ImageDetail> additionalImages = bookImageService.getAdditionalImages(book).stream()
                .map(image -> new ImageDetail(image.getId(), image.getFilePath()))
                .toList();
        bookDetailDto.setExistingAdditionalImages(additionalImages);
        return bookDetailDto;
    }

    public void updateEntityFromDto(Book book, BookRequestDto bookDto) {
        book.setTitle(bookDto.getTitle());
        book.setExplanation(bookDto.getDescription());
        book.setContents(bookDto.getContents());
        book.setIsbn(bookDto.getIsbn());
        book.setSalePrice(bookDto.getPriceSales());
        book.setRegularPrice(bookDto.getPriceStandard());
        book.setPublishDate(bookDto.getPubDate());
        book.setStock(bookDto.getStock());
        book.setSale(bookDto.isSale());
        book.setPackaging(bookDto.isPackaging());

        // Publisher 업데이트
        book.setPublisher(publisherService.getOrCreatePublisher(bookDto.getPublisher()));

        // Series 업데이트
        if (StringUtils.isBlank(bookDto.getSeriesName())) {
            book.setSeries(seriesService.getOrCreateSeries(bookDto.getSeriesName()));
        }
        // Category 업데이트
        categoryService.updateBookCategories(book, bookDto.getCategoryIds());

        // 태그 업데이트
        if (StringUtils.isBlank(bookDto.getTags())) {
            List<Tag> tags = tagService.getOrCreateTagsByName(bookDto.getTags());
            book.clearBookTags();
            tags.forEach(tag -> book.addBookTag(new BookTag(tag)));
        }
    }

    private void addCategoriesAndTags(Book book, List<Long> categoryIds, String tags) {
        if (!CollectionUtils.isEmpty(categoryIds)) {
            categoryService.createBookCategories(categoryIds).forEach(book::addBookCategory);
        }

        if (StringUtils.isBlank(tags)) {
            tagService.createBookTags(tagService.getOrCreateTagsByName(tags)).forEach(book::addBookTag);
        }
    }
}
