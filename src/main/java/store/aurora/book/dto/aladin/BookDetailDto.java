package store.aurora.book.dto.aladin;

import lombok.*;
import store.aurora.book.dto.category.CategoryResponseDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookDetailDto {

    private String title;

    private String author;

    private String description;

    private String contents;

    private String publisher;

    private LocalDate pubDate;

    private String isbn;

    private int priceSales;

    private int priceStandard;
    private ImageDetail cover;
    private List<ImageDetail> existingAdditionalImages; // 부가 이미지 URL 리스트

    private int stock = 100;
    private boolean isSale = false;
    private boolean isPackaging = false;

    private String seriesName;

    private List<CategoryResponseDTO> categories = new ArrayList<>();

    private String tags;

}
