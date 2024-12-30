package store.aurora.book.dto.aladin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDto {
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
    private Boolean isForSale = false;
    private Boolean isPackaged = false;
    private SeriesInfo seriesInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SeriesInfo {
        private String seriesName;
    }
    private List<Long> categoryIds; // 선택된 카테고리 ID 리스트

}