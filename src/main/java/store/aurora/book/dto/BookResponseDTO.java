package store.aurora.book.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BookResponseDTO {
    private Long id;
    private String title;
    private int regularPrice;
    private int salePrice;
    private boolean packaging;
    private Integer stock;
    private String explanation;
    private String contents;
    private String isbn;
    private LocalDate publishDate;
    private boolean isSale;
    private String publisherName;
    private String seriesName;
}