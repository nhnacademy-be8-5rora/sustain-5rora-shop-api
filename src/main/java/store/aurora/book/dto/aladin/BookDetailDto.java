package store.aurora.book.dto.aladin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BookDetailDto {
    private Long id;
    private String title;
    private String description;
    private LocalDate pubDate;
    private String isbn13;
    private int priceSales;
    private int priceStandard;
    private String contents;
    private boolean isForSale;
    private boolean isPackaged;
    private int stock;
    private String publisher;
    private String seriesName;
    private String thumbnailPath;
    private String authors;
}