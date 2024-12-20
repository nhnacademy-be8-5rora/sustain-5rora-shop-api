package store.aurora.book.mapper;

import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.dto.response.BookResponseDTO;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.category.BookCategory;
import store.aurora.book.entity.category.Category;
import store.aurora.book.entity.tag.BookTag;

import java.util.stream.Collectors;

public class BookMapper {

    // BookRequestDTO -> Book 엔티티 변환
    public static Book toEntity(BookRequestDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setRegularPrice(dto.getRegularPrice());
        book.setSalePrice(dto.getSalePrice());
        book.setPackaging(dto.isPackaging());
        book.setStock(dto.getStock());
        book.setExplanation(dto.getExplanation());
        book.setContents(dto.getContents());
        book.setIsbn(dto.getIsbn());
        book.setPublishDate(dto.getPublishDate());
        book.setSale(dto.isSale());
        return book;
    }

    // Book 엔티티 -> BookResponseDTO 변환
    public static BookResponseDTO toDTO(Book book) {
        return BookResponseDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .regularPrice(book.getRegularPrice())
                .salePrice(book.getSalePrice())
                .packaging(book.isPackaging())
                .stock(book.getStock())
                .explanation(book.getExplanation())
                .contents(book.getContents())
                .isbn(book.getIsbn())
                .publishDate(book.getPublishDate())
                .isSale(book.isSale())
                .publisherName(book.getPublisher() != null ? book.getPublisher().getName() : null)
                .seriesName(book.getSeries() != null ? book.getSeries().getName() : null)
                .categories(book.getBookCategories().stream()
                        .map(BookCategory::getCategory)
                        .map(Category::getName)
                        .collect(Collectors.toList()))
//                .tags(book.getBookTags().stream()
//                        .map(BookTag::getTag)
//                        .map(tag -> tag.getName())
//                        .collect(Collectors.toList()))
                .build();
    }
}
