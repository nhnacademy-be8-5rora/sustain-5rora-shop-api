package store.aurora.book.dto;


import lombok.*;
import store.aurora.book.dto.category.BookCategoryDto;
import store.aurora.book.dto.category.CategoryResponseDTO;

import java.time.LocalDate;
import java.util.List;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailsDto {
    private Long bookId;
    private String title;
    private String isbn;
    private int regularPrice;
    private int salePrice;
    private String explanation;
    private String contents;
    private LocalDate publishDate;
    private PublisherDto publisher;
    private List<BookImageDto> bookImages;
    private List<ReviewDto> reviews;
    private List<String> tagNames;
    private int likeCount;
    private List<BookCategoryDto> categoryPath;
    private double rating;
}

