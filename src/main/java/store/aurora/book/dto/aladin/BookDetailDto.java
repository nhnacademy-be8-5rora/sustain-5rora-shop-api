package store.aurora.book.dto.aladin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDetailDto {
    private Long id;
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
    private List<String> additionalImages; // 부가 이미지 URL 리스트
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
    private String tags; // 쉼표로 구분된 태그 이름 문자열
}
