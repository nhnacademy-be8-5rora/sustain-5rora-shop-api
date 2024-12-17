package store.aurora.book.mapper;

import store.aurora.book.dto.BookRequestDTO;
import store.aurora.book.dto.BookResponseDTO;
import store.aurora.book.entity.*;
import store.aurora.book.entity.category.Category;

import java.util.List;

public class BookMapper {

    public static Book toEntity(BookRequestDTO dto, Publisher publisher, Series series) {
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
        book.setPublisher(publisher);
        book.setSeries(series);
        return book;
    }

    public static BookResponseDTO toDTO(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setRegularPrice(book.getRegularPrice());
        dto.setSalePrice(book.getSalePrice());
        dto.setPackaging(book.isPackaging());
        dto.setStock(book.getStock());
        dto.setExplanation(book.getExplanation());
        dto.setContents(book.getContents());
        dto.setIsbn(book.getIsbn());
        dto.setPublishDate(book.getPublishDate());
        dto.setSale(book.isSale());
        dto.setPublisherName(book.getPublisher().getName());
        if (book.getSeries() != null) {
            dto.setSeriesName(book.getSeries().getName());
        }

        return dto;
    }
}