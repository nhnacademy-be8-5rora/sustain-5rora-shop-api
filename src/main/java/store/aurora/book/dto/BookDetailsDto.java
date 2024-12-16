package store.aurora.book.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailsDto {
    // 책 아이디
    private Long bookId;
    // 책 표지

    // 책 이름
    private String bookName;
    // 평균 평점
    private Long rating;
    // 정가
    private Integer regularPrice;
    // 판매가
    private Integer salePrice;
    // 배송비
    private Integer deliveryPrice;
    // 작가 아이디
    private Long authorId;
    // 작가 이름
    private String authorName;
    // 카테고리

    // 태그
    List<String> tagList;
    // 조회수
    private Long views;
    // 좋아요
    private Long likes;
    // 리뷰

    // 리뷰 이미지

}
