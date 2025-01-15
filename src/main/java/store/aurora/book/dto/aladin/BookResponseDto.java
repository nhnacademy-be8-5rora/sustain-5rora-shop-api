package store.aurora.book.dto.aladin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookResponseDto {
    private Long id;
    private String title;
    private String author;
    private String description;
    private String publisher;
    private String pubDate;
    private String isbn13;
    private int priceSales;
    private int priceStandard;
    private String cover;
    private int stock;
    private boolean isSale = false;
    private boolean isPackaging = false;
}