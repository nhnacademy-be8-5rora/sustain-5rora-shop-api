package store.aurora.book.dto.aladin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "도서 요청 DTO (사용자가 도서를 등록할 때 사용하는 요청 객체)")
public class BookRequestDto {

    @Schema(description = "도서 제목", example = "해리포터와 마법사의 돌")
    @NotBlank(message = "제목은 필수 항목입니다.")
    @Size(max = 150, message = "제목은 최대 150자까지 입력 가능합니다.")
    private String title;

    @Schema(description = "저자 및 역할 정보", example = "J.K. 롤링 (지은이)")
    @NotBlank(message = "저자는 필수 항목입니다.")
    @Size(max = 500, message = "저자와 역할 정보는 최대 500자까지 입력 가능합니다.")
    private String author;

    @Schema(description = "도서 설명", example = "마법 세계에서 펼쳐지는 이야기")
    @NotBlank(message = "책 설명은 필수 항목입니다.")
    @Size(max = 10000, message = "책 설명은 최대 10000자까지 입력 가능합니다.")
    private String description;

    @Schema(description = "도서 목차", example = "1. 마법사의 돌\n2. 호그와트로 가는 길...")
    @Size(max = 5000, message = "책 목차은 최대 5000자까지 입력 가능합니다.")
    private String contents;

    @Schema(description = "출판사", example = "문학동네")
    @NotBlank(message = "출판사는 필수 항목입니다.")
    @Size(max = 50, message = "출판사 이름은 최대 50자까지 입력 가능합니다.")
    private String publisher;

    @Schema(description = "출판 날짜 (yyyy-MM-dd)", example = "2000-11-01")
    @NotNull(message = "출판 날짜는 필수 항목입니다.")
    @PastOrPresent(message = "출판일은 과거 또는 현재 날짜여야 합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate pubDate;

    @Schema(description = "ISBN (10자리 또는 13자리)", example = "9788995975441")
    @NotBlank(message = "ISBN은 필수 항목입니다.")
    @Pattern(regexp = "^(?:[a-zA-Z0-9]{10}|\\d{13})$", message = "ISBN은 10자리(알파벳 포함 가능) 또는 13자리 숫자여야 합니다.")
    private String isbn;

    @Schema(description = "판매 가격 (원)", example = "13500")
    @Positive(message = "판매 가격은 양수여야 합니다.")
    private int priceSales;

    @Schema(description = "정가 (원)", example = "15000")
    @Positive(message = "정가 가격은 양수여야 합니다.")
    private int priceStandard;

    @Schema(description = "도서 재고 수량", example = "100")
    @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    private int stock = 100;

    @Schema(description = "판매 여부", example = "true")
    private boolean isSale = false;

    @Schema(description = "포장 가능 여부", example = "false")
    private boolean isPackaging = false;

    @Schema(description = "시리즈 이름", example = "해리포터 시리즈")
    @Size(max = 100, message = "시리즈 이름은 최대 100자까지 입력 가능합니다.")
    private String seriesName;

    @Schema(description = "카테고리 ID 리스트", example = "[1, 2, 3]")
    @NotNull(message = "카테고리는 필수 항목입니다.")
    @Size(min = 1, max = 10, message = "카테고리는 최소 1개에서 최대 10개까지 선택 가능합니다.")
    private List<Long> categoryIds;

    @Schema(description = "태그 (쉼표로 구분된 문자열)", example = "베스트셀러, 판타지")
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