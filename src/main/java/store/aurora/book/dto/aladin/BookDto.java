package store.aurora.book.dto.aladin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // SeriesInfo를 별도의 클래스에 매핑
    private SeriesInfo seriesInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SeriesInfo {
        private String seriesName; // 시리즈 이름
    }
}
