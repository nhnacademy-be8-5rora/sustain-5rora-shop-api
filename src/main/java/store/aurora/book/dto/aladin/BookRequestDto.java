package store.aurora.book.dto.aladin;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDto {

    @NotBlank(message = "제목은 필수 항목입니다.")
    @Size(max = 150, message = "제목은 최대 150자까지 입력 가능합니다.")
    private String title;

    @NotBlank(message = "저자는 필수 항목입니다.")
    @Size(max = 500, message = "저자와 역할 정보는 최대 500자까지 입력 가능합니다.")
    private String author;

    @NotBlank(message = "책 설명은 필수 항목입니다.")
    @Size(max = 10000, message = "책 설명은 최대 10000자까지 입력 가능합니다.")
    private String description;

    @Size(max = 5000, message = "책 목차은 최대 5000자까지 입력 가능합니다.")
    private String contents;

    @NotBlank(message = "출판사는 필수 항목입니다.")
    @Size(max = 50, message = "출판사 이름은 최대 50자까지 입력 가능합니다.")
    private String publisher;

    @NotNull(message = "출판 날짜는 필수 항목입니다.") // null 값 허용하지 않음
    @PastOrPresent(message = "출판일은 과거 또는 현재 날짜여야 합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate pubDate;

    @NotBlank(message = "ISBN은 필수 항목입니다.")
    @Pattern(regexp = "^(?:[a-zA-Z0-9]{10}|\\d{13})$", message = "ISBN은 10자리(알파벳 포함 가능) 또는 13자리 숫자여야 합니다.")
    private String isbn;

    @Positive(message = "판매 가격은 양수여야 합니다.")
    private int priceSales;

    @Positive(message = "정가 가격은 양수여야 합니다.")
    private int priceStandard;

    @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    private int stock = 100;

    private boolean isSale = false;

    private boolean isPackaging = false;

    @Size(max = 100, message = "시리즈 이름은 최대 100자까지 입력 가능합니다.")
    private String seriesName;

    @NotNull(message = "카테고리는 필수 항목입니다.")
    @Size(min = 1, max = 10, message = "카테고리는 최소 1개에서 최대 10개까지 선택 가능합니다.")
    private List<Long> categoryIds;

    @Size(max = 200, message = "태그 입력은 최대 200자까지 가능합니다.")
    private String tags; // 선택 항목

    public BookRequestDto(String title, String author, String description, String publisher, LocalDate pubDate, String isbn, int priceSales, int priceStandard, List<Long> categoryIds) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.publisher = publisher;
        this.pubDate = pubDate;
        this.isbn = isbn;
        this.priceSales = priceSales;
        this.priceStandard = priceStandard;
        this.categoryIds = categoryIds;
    }

}