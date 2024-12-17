package store.aurora.search.dto;


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
public class BookSearchResponseDTO {

    private Long id;
    private String title;
    private int regularPrice;
    private int salePrice;

    private LocalDate publishDate;

    private String publisherName;


    private String imgPath;

    private List<AuthorDTO> authors;

    private List<String> categoryNames;
    private Long viewCount;
    private int reviewCount;
    private double reviewRating; // 리뷰 평점

    public BookSearchResponseDTO(BookSearchEntityDTO book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.regularPrice = book.getRegularPrice();
        this.salePrice = book.getSalePrice();
        this.publishDate = book.getPublishDate();
        this.publisherName = book.getPublisherName();
        this.imgPath = book.getImgPath();

        this.authors = book.getAuthors();
        this.categoryNames = book.getCategoryNameList();
        this.viewCount = book.getViewCount();
        this.reviewCount = book.getReviewCount();
        this.reviewRating = book.getReviewRating();
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
                ", categoryNames=" + categoryNames +  // categoryNames 추가
                ", viewCount=" + viewCount +  // viewCount 추가
                ", reviewCount=" + reviewCount +
                ", reviewRating=" + reviewRating + "]";
    }



}
