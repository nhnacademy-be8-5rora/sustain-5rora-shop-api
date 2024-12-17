package store.aurora.book.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookDetailsUpdateDTO {
    private String title;
    private int regularPrice;
    private String explanation;
    private String contents;
    private String isbn;
    private LocalDate publishDate;
    private String publisherName;
    private String seriesName;
    private boolean isSale;
}
