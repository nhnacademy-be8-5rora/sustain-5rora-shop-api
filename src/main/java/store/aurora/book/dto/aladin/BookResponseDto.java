package store.aurora.book.dto.aladin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "도서 응답 DTO")
public class BookResponseDto {

    @Schema(description = "도서 ID", example = "1")
    private Long id;

    @Schema(description = "도서 제목", example = "해리포터와 마법사의 돌")
    private String title;

    @Schema(description = "저자", example = "J.K. 롤링")
    private String author;

    @Schema(description = "도서 설명", example = "마법 세계에서 펼쳐지는 이야기")
    private String description;

    @Schema(description = "출판사", example = "문학동네")
    private String publisher;

    @Schema(description = "출판 날짜 (yyyy-MM-dd)", example = "2000-11-01")
    private String pubDate;

    @Schema(description = "ISBN-13", example = "9788995975441")
    private String isbn13;

    @Schema(description = "판매 가격", example = "13500")
    private int priceSales;

    @Schema(description = "정가", example = "15000")
    private int priceStandard;

    @Schema(description = "도서 표지 URL", example = "https://image.aladin.co.kr/...jpg")
    private String cover;

    @Schema(description = "도서 재고 수량", example = "100")
    private int stock;

    @Schema(description = "판매 여부", example = "true")
    private boolean isSale = false;

    @Schema(description = "포장 여부", example = "false")
    private boolean isPackaging = false;

    public BookResponseDto(Long id, String title, String author, String description, String publisher, String pubDate, String isbn13) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.publisher = publisher;
        this.pubDate = pubDate;
        this.isbn13 = isbn13;
    }
}