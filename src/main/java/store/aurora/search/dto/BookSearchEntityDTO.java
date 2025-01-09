package store.aurora.search.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.aurora.book.dto.AuthorDTO;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Setter
@NoArgsConstructor
@Getter
public class BookSearchEntityDTO {

    private Long id;
    private String title;
    private Integer regularPrice;
    private Integer salePrice;

    private LocalDate publishDate;
    private String publisherName;

    private List<AuthorDTO> authors; // 변경된 부분

    private String imgPath;

    private List<Long> categoryIdList; // 카테고리 이름 리스트 추가

    private Long viewCount;
    private Integer reviewCount;
    private Double reviewRating; // 리뷰 평점
    private boolean isSale;

    // 빌더 클래스
    public static class Builder {
        private Long id;
        private String title;
        private Integer regularPrice;
        private Integer salePrice;
        private Boolean isSale;
        private LocalDate publishDate;
        private String publisherName;
        private String authors;
        private String bookImagePath;
        private String categories;
        private Long viewCount;
        private Integer reviewCount;
        private Double reviewRating;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder regularPrice(Integer regularPrice) { this.regularPrice = regularPrice; return this; }
        public Builder salePrice(Integer salePrice) { this.salePrice = salePrice; return this; }
        public Builder isSale(Boolean isSale) { this.isSale = isSale; return this; }
        public Builder publishDate(LocalDate publishDate) { this.publishDate = publishDate; return this; }
        public Builder publisherName(String publisherName) { this.publisherName = publisherName; return this; }
        public Builder authors(String authors) { this.authors = authors; return this; }
        public Builder bookImagePath(String bookImagePath) { this.bookImagePath = bookImagePath; return this; }
        public Builder categories(String categories) { this.categories = categories; return this; }
        public Builder viewCount(Long viewCount) { this.viewCount = viewCount; return this; }
        public Builder reviewCount(Integer reviewCount) { this.reviewCount = reviewCount; return this; }
        public Builder averageReviewRating(Double averageReviewRating) { this.reviewRating = averageReviewRating; return this; }

        public BookSearchEntityDTO build() {
            return new BookSearchEntityDTO(this);
        }
    }
    // 생성자
    private BookSearchEntityDTO(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.regularPrice = builder.regularPrice;
        this.salePrice = builder.salePrice;
        this.isSale = builder.isSale;
        this.publishDate = builder.publishDate;
        this.publisherName = builder.publisherName;
        this.authors = convertAuthorsStringToList(builder.authors);
        this.imgPath = builder.bookImagePath;
        this.categoryIdList =convertCategoryIdsToList( builder.categories);
        this.viewCount = builder.viewCount;
        this.reviewCount = builder.reviewCount;
        this.reviewRating = builder.reviewRating;
    }


    public List<Long> convertCategoryIdsToList(String categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyList(); // 빈 리스트 반환
        }
        return Arrays.stream(categoryIds.split(","))
                .map(Long::parseLong)
                .toList();
    }

    public void setAuthors(String authors) {
        this.authors = convertAuthorsStringToList(authors);
    }

    public void setCategoryIdList(String categoryIdList) {
        this.categoryIdList = convertCategoryIdsToList(categoryIdList);
    }

    // 쉼표로 구분된 문자열을 List<AuthorDTO>로 변환
    private List<AuthorDTO> convertAuthorsStringToList(String authorsString) {
        if (authorsString == null || authorsString.isEmpty()) {
            return List.of(); // 비어 있으면 빈 리스트 반환
        }

        // 정규식 기반으로 저자 데이터를 안전하게 분리
        return Arrays.stream(authorsString.split("\\s*,\\s*")) // 콤마와 공백 기준으로 분리
                .map(this::parseAuthor) // 각 저자 문자열을 AuthorDTO로 변환
                .toList();
    }

    private AuthorDTO parseAuthor(String authorString) {
        // 정규식으로 "이름 (역할)" 분리
        Pattern pattern = Pattern.compile("^(.*?)\\s*\\((.*?)\\)$");
        Matcher matcher = pattern.matcher(authorString);

        String name;
        String role = null;

        if (matcher.find()) {
            name = matcher.group(1).trim(); // 이름 추출
            role = matcher.group(2).trim(); // 역할 추출
        } else {
            // 역할이 없는 경우
            name = authorString.trim();
        }


        return new AuthorDTO(name, role);
    }
    @Override
    public String toString() {
        return "BookSearchEntityDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", regularPrice=" + regularPrice +
                ", salePrice=" + salePrice +
                ", isSale=" + isSale +
                ", publishDate=" + publishDate +
                ", publisherName='" + publisherName + '\'' +
                ", authors=" + authors +
                ", imgPath='" + imgPath + '\'' +
                ", categoryIdList=" + categoryIdList + // 카테고리 이름 리스트 추가
                ", viewCount=" + viewCount + // viewCount 추가
                ", reviewCount=" + reviewCount+
                ", reviewRating=" + reviewRating +
                '}';
    }



}
