package store.aurora.book.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookInfoDTO {
    private String title;
    private int regularPrice;
    private int salePrice;
    private Integer stock;
    private boolean isSale;
    private String filePath;
}