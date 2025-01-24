package store.aurora.book.dto.aladin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import store.aurora.book.dto.category.CategoryResponseDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "도서 상세 정보 DTO")
public class BookDetailDto {

    @Schema(description = "도서 제목", example = "해리포터와 마법사의 돌")
    private String title;

    @Schema(description = "저자", example = "J.K. 롤링")
    private String author;

    @Schema(description = "도서 설명", example = "마법 세계에서 펼쳐지는 이야기")
    private String description;

    @Schema(description = "도서 목차", example = "1. 마법사의 돌\n2. 호그와트로 가는 길...")
    private String contents;

    @Schema(description = "출판사", example = "문학동네")
    private String publisher;

    @Schema(description = "출판 날짜 (yyyy-MM-dd)", example = "2000-11-01")
    private LocalDate pubDate;

    @Schema(description = "ISBN", example = "9788995975441")
    private String isbn;

    @Schema(description = "판매 가격 (원)", example = "13500")
    private int priceSales;

    @Schema(description = "정가 (원)", example = "15000")
    private int priceStandard;

    @Schema(description = "도서 표지 이미지")
    private ImageDetail cover;

    @Schema(description = "부가 이미지 URL 리스트")
    private List<ImageDetail> existingAdditionalImages; // 부가 이미지 URL 리스트

    @Schema(description = "도서 재고 수량", example = "100")
    private int stock = 100;

    @Schema(description = "판매 여부", example = "true")
    private boolean isSale = false;

    @Schema(description = "포장 가능 여부", example = "false")
    private boolean isPackaging = false;

    @Schema(description = "시리즈 이름", example = "해리포터 시리즈")
    private String seriesName;

    @Schema(description = "카테고리 정보 리스트")
    private List<CategoryResponseDTO> categories = new ArrayList<>();

    @Schema(description = "태그 (쉼표로 구분된 문자열)", example = "베스트셀러, 판타지")
    private String tags;
}