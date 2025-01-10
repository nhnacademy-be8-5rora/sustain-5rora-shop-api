package store.aurora.book.dto.aladin;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDto {
    private String title;
    private String author;
    private String description;
    private String contents;
    private String publisher;
    private String pubDate;
    private String isbn13;
    private int priceSales;
    private int priceStandard;
    private String cover;
    private int stock;
    private Boolean isForSale = false;
    private Boolean isPackaged = false;
    private String seriesName; // 시리즈 이름
    private List<Long> categoryIds;
    private String tags;
}