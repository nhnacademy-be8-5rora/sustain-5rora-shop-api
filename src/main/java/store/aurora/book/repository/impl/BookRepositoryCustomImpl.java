package store.aurora.book.repository.impl;


import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import store.aurora.book.entity.Book;
import store.aurora.book.repository.BookRepositoryCustom;
import store.aurora.search.dto.BookSearchEntityDTO;
import store.aurora.utils.ValidationUtils;

import static store.aurora.book.entity.QBook.book;
import static store.aurora.book.entity.QBookAuthor.bookAuthor;
import static store.aurora.book.entity.QBookView.bookView;
import static store.aurora.book.entity.QPublisher.publisher;
import static store.aurora.book.entity.QAuthor.author;
import static store.aurora.book.entity.QAuthorRole.authorRole;
import static store.aurora.book.entity.QBookImage.bookImage;
import static store.aurora.book.entity.category.QCategory.category;
import static store.aurora.book.entity.category.QBookCategory.bookCategory;
import static store.aurora.review.entity.QReview.review;
import static store.aurora.utils.ValidationUtils.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Slf4j
public class BookRepositoryCustomImpl extends QuerydslRepositorySupport implements BookRepositoryCustom {


    public BookRepositoryCustomImpl() {
        super(Book.class);
    }
    //특정 String을 포함하는 제목을 가진 책들을 조인해서 값들을 반환.
    @Override
    public Page<BookSearchEntityDTO> findBooksByTitleWithDetails(String title, Pageable pageable) {
        if (Objects.isNull(title) || title.isBlank()) {
            return emptyPage(pageable);
        }

        // 이미지를 하나만 가져오는 쿼리
        var bookImagePathSubquery = JPAExpressions.select(bookImage.filePath)
                .from(bookImage)
                .where(bookImage.book.id.eq(book.id))
                .orderBy(bookImage.id.asc())
                .limit(1);

        // 조회수를 가져오는 서브쿼리
        var viewCountSubquery = JPAExpressions.select(bookView.count())
                .from(bookView)
                .where(bookView.book.id.eq(book.id));

        //리뷰 개수 가져오는 쿼리
        var reviewCountSubquery = JPAExpressions.select(review.count().intValue()) // int로 변환
                .from(review)
                .where(review.book.id.eq(book.id));

        // 평균 리뷰 점수 가져오는 서브쿼리
        var averageReviewRatingSubquery = JPAExpressions.select(review.reviewRating.avg())
                .from(review)
                .where(review.book.id.eq(book.id));

        // bookDetail 가져오기.
        List<BookSearchEntityDTO> content = from(book)
                .leftJoin(book.publisher, publisher)
                .where(book.title.like("%" + title + "%"))
                .groupBy(book.id, book.title, book.regularPrice, book.salePrice, book.publishDate, publisher.name)
                .select(Projections.constructor(
                        BookSearchEntityDTO.class,
                        book.id,
                        book.title,
                        book.regularPrice,
                        book.salePrice,
                        book.publishDate,
                        publisher.name,
                        // 여러 저자와 역할을 쉼표로 묶어서 반환
                        JPAExpressions.select(Expressions.stringTemplate(
                                        "GROUP_CONCAT({0} || ' (' || {1} || ')')",
                                        author.name, authorRole.role
                                ))
                                .from(bookAuthor)
                                .leftJoin(bookAuthor.author, author)
                                .leftJoin(bookAuthor.authorRole, authorRole)
                                .where(bookAuthor.book.id.eq(book.id)),
                        bookImagePathSubquery,
                        // 카테고리 이름을 List로 변환
                        JPAExpressions.select(Expressions.stringTemplate(
                                        "GROUP_CONCAT({0})",
                                        category.name
                                ))
                                .from(bookCategory)
                                .leftJoin(bookCategory.category, category)
                                .where(bookCategory.book.id.eq(book.id)),
                        viewCountSubquery,
                        reviewCountSubquery, // 리뷰 갯수 추가
                        averageReviewRatingSubquery // 평균 리뷰 점수 추가


                ))
                .fetch();

        log.debug("customImpl 메서드 값 확인 {}",content.toString());

        // Count query for pagination
        long total = from(book)
                .where(book.title.like("%" + title + "%"))
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    //특정 String을 포함하는 작가이름을 가진 책들을 조인해서 값들을 반환.
    @Override
    public Page<BookSearchEntityDTO> findBooksByAuthorNameWithDetails(String name, Pageable pageable) {

        if (Objects.isNull(name) || name.isBlank()) {
            return emptyPage(pageable);
        }

        // 서브쿼리: 첫 번째 이미지 경로 가져오기
        var bookImagePathSubquery = JPAExpressions.select(bookImage.filePath)
                .from(bookImage)
                .where(bookImage.book.id.eq(book.id))
                .orderBy(bookImage.id.asc())
                .limit(1);

        // 조회수를 가져오는 서브쿼리
        var viewCountSubquery = JPAExpressions.select(bookView.count())
                .from(bookView)
                .where(bookView.book.id.eq(book.id));

        //리뷰 개수
        var reviewCountSubquery = JPAExpressions.select(review.count().intValue()) // int로 변환
                .from(review)
                .where(review.book.id.eq(book.id));

        // 평균 리뷰 점수 가져오는 서브쿼리
        var averageReviewRatingSubquery = JPAExpressions.select(review.reviewRating.avg())
                .from(review)
                .where(review.book.id.eq(book.id));

        // 메인 쿼리: 저자 이름 검색 및 도서 정보 조회
        List<BookSearchEntityDTO> content = from(book)
                .leftJoin(book.publisher, publisher)
                .leftJoin(bookAuthor).on(bookAuthor.book.id.eq(book.id))
                .leftJoin(bookAuthor.author, author)
                .leftJoin(bookAuthor.authorRole, authorRole)
                .where(author.name.like("%" + name + "%")) // 저자 이름 조건
                .groupBy(book.id, book.title, book.regularPrice, book.salePrice, book.publishDate, publisher.name)
                .orderBy(book.title.asc()) // 정렬 조건
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .select(Projections.constructor(
                        BookSearchEntityDTO.class,
                        book.id,
                        book.title,
                        book.regularPrice,
                        book.salePrice,
                        book.publishDate,
                        publisher.name,
                        // 여러 저자와 역할을 쉼표로 묶어서 반환
                        JPAExpressions.select(Expressions.stringTemplate(
                                        "GROUP_CONCAT({0} || ' (' || {1} || ')')",
                                        author.name, authorRole.role
                                ))
                                .from(bookAuthor)
                                .leftJoin(bookAuthor.author, author)
                                .leftJoin(bookAuthor.authorRole, authorRole)
                                .where(bookAuthor.book.id.eq(book.id)),
                        bookImagePathSubquery,
                        // 카테고리 이름을 List로 변환
                        JPAExpressions.select(Expressions.stringTemplate(
                                        "GROUP_CONCAT({0})",
                                        category.name
                                ))
                                .from(bookCategory)
                                .leftJoin(bookCategory.category, category)
                                .where(bookCategory.book.id.eq(book.id)),
                        viewCountSubquery, // 조회수 추가
                        reviewCountSubquery, // 리뷰 갯수 추가
                        averageReviewRatingSubquery // 평균 리뷰 점수 추가

                ))
                .fetch();

        // 총 데이터 수 계산
        long total = from(book)
                .leftJoin(bookAuthor).on(bookAuthor.book.id.eq(book.id))
                .leftJoin(bookAuthor.author, author)
                .where(author.name.like("%" + name + "%")) // 저자 이름 조건
                .fetchCount();

        log.debug("BookRepositoryCustomImpl 확인:");

        // 페이지 처리된 결과 반환
        return new PageImpl<>(content, pageable, total);
    }


    // 특정 카테고리이름을 가진 책들을 반환.
    @Override
    public Page<BookSearchEntityDTO> findBooksByCategoryNameWithDetails(String categoryName, Pageable pageable) {
        if (categoryName == null || categoryName.isBlank()) {
            return emptyPage(pageable);
        }

        // 카테고리 이름으로 해당 카테고리의 ID를 가져오는 서브쿼리
        var categoryIdSubquery = JPAExpressions.select(category.id)
                .from(category)
                .where(category.name.eq(categoryName));

        // 최하위 카테고리인지 확인하기 위한 서브쿼리
        var isLeafCategorySubquery = JPAExpressions.select(category.parent.id)
                .from(category)
                .where(category.id.eq(categoryIdSubquery));

        // 최하위 카테고리일 경우 해당 카테고리만 가져오고, 그렇지 않으면 해당 카테고리와 하위 카테고리들을 모두 가져오기
        var categoryHierarchySubquery = JPAExpressions.select(category.id)
                .from(category)
                .where(
                        category.id.in(categoryIdSubquery) // 현재 카테고리 포함
                                .or(category.parent.id.in(categoryIdSubquery)) // 부모 카테고리의 자식 카테고리들 포함
                                .or(
                                        // 최하위 카테고리일 경우 본인만 포함
                                        category.id.eq(categoryIdSubquery)
                                                .and(category.parent.id.isNull()) // 부모가 없는 최하위 카테고리 확인
                                )
                )
                .distinct();
        //리뷰 개수
        var reviewCountSubquery = JPAExpressions.select(review.count().intValue()) // int로 변환
                .from(review)
                .where(review.book.id.eq(book.id));

        // 평균 리뷰 점수 가져오는 서브쿼리
        var averageReviewRatingSubquery = JPAExpressions.select(review.reviewRating.avg())
                .from(review)
                .where(review.book.id.eq(book.id));


        // 서브쿼리: 첫 번째 이미지 경로 가져오기
        var bookImagePathSubquery = JPAExpressions.select(bookImage.filePath)
                .from(bookImage)
                .where(bookImage.book.id.eq(book.id))
                .orderBy(bookImage.id.asc())
                .limit(1);

        // 조회수를 가져오는 서브쿼리
        var viewCountSubquery = JPAExpressions.select(bookView.count())
                .from(bookView)
                .where(bookView.book.id.eq(book.id));

        // 메인 쿼리: 카테고리와 자식 카테고리들을 포함하여 책들을 검색
        List<BookSearchEntityDTO> content = from(book)
                .leftJoin(book.publisher, publisher)
                .leftJoin(bookCategory).on(bookCategory.book.id.eq(book.id))
                .leftJoin(bookCategory.category, category)
                .where(bookCategory.category.id.in(categoryHierarchySubquery)) // 카테고리와 자식 카테고리 포함
                .groupBy(book.id, book.title, book.regularPrice, book.salePrice, book.publishDate, publisher.name)
                .orderBy(book.title.asc()) // 정렬 조건
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .select(Projections.constructor(
                        BookSearchEntityDTO.class,
                        book.id,
                        book.title,
                        book.regularPrice,
                        book.salePrice,
                        book.publishDate,
                        publisher.name,
                        // 여러 저자와 역할을 쉼표로 묶어서 반환
                        JPAExpressions.select(Expressions.stringTemplate(
                                        "GROUP_CONCAT({0} || ' (' || {1} || ')')",
                                        author.name, authorRole.role
                                ))
                                .from(bookAuthor)
                                .leftJoin(bookAuthor.author, author)
                                .leftJoin(bookAuthor.authorRole, authorRole)
                                .where(bookAuthor.book.id.eq(book.id)),
                        bookImagePathSubquery,
                        // 카테고리 이름을 List로 변환
                        JPAExpressions.select(Expressions.stringTemplate(
                                        "GROUP_CONCAT({0})",
                                        category.name
                                ))
                                .from(bookCategory)
                                .leftJoin(bookCategory.category, category)
                                .where(bookCategory.book.id.eq(book.id)),
                        viewCountSubquery, // 조회수 추
                        reviewCountSubquery, // 리뷰 갯수 추가
                        averageReviewRatingSubquery // 평균 리뷰 점수 추가


                ))
                .fetch();

        // 총 데이터 수 계산
        long total = from(book)
                .leftJoin(bookCategory).on(bookCategory.book.id.eq(book.id))
                .leftJoin(bookCategory.category, category)
                .where(bookCategory.category.id.in(categoryHierarchySubquery)) // 카테고리와 자식 카테고리 포함
                .fetchCount();

        // 페이지 처리된 결과 반환
        return new PageImpl<>(content, pageable, total);
    }




}
