package store.aurora.search.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.aurora.book.dto.AuthorDTO;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Schema(description = "책 검색 결과 응답 DTO")
public class BookSearchResponseDTO {

    @Schema(description = "책 ID", example = "1")
    private Long id;

    @Schema(description = "책 제목", example = "Java Programming Guide")
    private String title;

    @Schema(description = "정가", example = "20000")
    private int regularPrice;

    @Schema(description = "판매가", example = "18000")
    private int salePrice;

    @Schema(description = "출판 날짜", type = "string", format = "date", example = "2024-01-15")
    private LocalDate publishDate;

    @Schema(description = "출판사 이름", example = "Sample Publisher")
    private String publisherName;

    @Schema(description = "책 이미지 경로", example = "/images/book.jpg")
    private String imgPath;

    @Schema(description = "저자 목록")
    private List<AuthorDTO> authors;

    @Schema(description = "카테고리 ID 목록")
    private List<Long> categoryIdList;

    @Schema(description = "조회수", example = "1000")
    private Long viewCount;

    @Schema(description = "리뷰 개수", example = "15")
    private int reviewCount;

    @Schema(description = "리뷰 평점", example = "4.8")
    private double reviewRating;

    @Schema(description = "좋아요 여부", example = "true")
    private boolean liked;

    @Schema(description = "판매 여부", example = "true")
    private boolean isSale;
    public BookSearchResponseDTO(BookSearchEntityDTO book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.regularPrice = book.getRegularPrice();
        this.salePrice = book.getSalePrice();
        this.publishDate = book.getPublishDate();
        this.publisherName = book.getPublisherName();
        this.imgPath = book.getImgPath();

        this.authors = book.getAuthors();
        this.categoryIdList = book.getCategoryIdList();
        this.viewCount = book.getViewCount();
        this.reviewCount = book.getReviewCount();
        this.reviewRating = book.getReviewRating();
        this.liked=false;
        this.isSale=book.isSale();
    }

    @Override
    public String toString() {
        return "BookSearchResponseDTO [id=" + id +
                ", title=" + title +
                ", regularPrice=" + regularPrice +
                ", salePrice=" + salePrice +
                ", publishDate=" + publishDate +
                ", publisherName=" + publisherName +
                ", imgPath=" + imgPath +
                ", authors=" + authors +
                ", categoryIdList=" + categoryIdList +  // categoryNames 추가
                ", viewCount=" + viewCount +  // viewCount 추가
                ", reviewCount=" + reviewCount +
                ", reviewRating=" + reviewRating +
                ", liked=" + liked +
                ", isSale=" + isSale +"]";
    }



}
