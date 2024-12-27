package store.aurora.book.dto.aladin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDtoEx {
    private String title;
    private String author;
    private String isbn13;
    private String publisher;
    private String pubDate;
    private String seriesName;
    private String description;
    private String contents;
    private int priceStandard;
    private int priceSales;
    private int stock;
    private boolean isForSale;
    private boolean isPackaged;
    private List<MultipartFile> uploadedImages;
    private BookDto.SeriesInfo seriesInfo;
    private String cover;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SeriesInfo {
        private String seriesName; // 시리즈 이름
    }
}