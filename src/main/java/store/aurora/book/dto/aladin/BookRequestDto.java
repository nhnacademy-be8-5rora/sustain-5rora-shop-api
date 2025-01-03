package store.aurora.book.dto.aladin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
    private Boolean isForSale = false;  // 기본값 설정
    private Boolean isPackaged = false; // 기본값 설정
    // SeriesInfo를 별도의 클래스에 매핑
    private SeriesInfo seriesInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SeriesInfo {
        private String seriesName; // 시리즈 이름
    }
    private List<Long> categoryIds; // 선택된 카테고리 ID 리스트
    private List<String> tagNames; // 입력된 태그 이름 리스트

}