package store.aurora.book.repository.impl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import store.aurora.book.dto.*;
import store.aurora.book.dto.category.BookCategoryDto;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.category.Category;
import store.aurora.book.entity.QBook;
import store.aurora.book.repository.BookRepositoryCustom;
import store.aurora.search.dto.BookSearchEntityDTO;

import static store.aurora.book.entity.QBook.book;
import static store.aurora.book.entity.QBookAuthor.bookAuthor;
import static store.aurora.book.entity.QBookView.bookView;
import static store.aurora.book.entity.category.QBookCategory.bookCategory;
import static store.aurora.book.entity.category.QCategory.category;
import static store.aurora.book.entity.QLike.like;
import static store.aurora.book.entity.QPublisher.publisher;
import static store.aurora.book.entity.QAuthor.author;
import static store.aurora.book.entity.QAuthorRole.authorRole;
import static store.aurora.book.entity.QBookImage.bookImage;
import static store.aurora.book.entity.tag.QBookTag.bookTag;
import static store.aurora.book.entity.tag.QTag.tag;
import static store.aurora.review.entity.QReview.review;
import static store.aurora.review.entity.QReviewImage.reviewImage;
import static store.aurora.user.entity.QUser.user;


import static store.aurora.utils.ValidationUtils.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Objects;


@Slf4j
public class BookRepositoryCustomImpl extends QuerydslRepositorySupport implements BookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public BookRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        super(Book.class);
        this.queryFactory = queryFactory;
    }

    //특정 String을 포함하는 제목을 가진 책들을 조인해서 값들을 반환.
    @Override
    public Page<BookSearchEntityDTO> findBooksByTitleWithDetails(String title, Pageable pageable) {
        // title이 null이거나 공백인 경우 빈 페이지 반환
        if (Objects.isNull(title)) {
            return emptyPage(pageable);
        }

        // 필터링 조건 정의
        BooleanExpression titleCondition = title.isBlank()
                ? null // 조건 없음
                : book.title.like("%" + title + "%"); // 제목 필터링 조건

        // 이미지를 하나만 가져오는 쿼리
        var bookImagePathSubquery = JPAExpressions.select(bookImage.filePath)
                .from(bookImage)
                .where(bookImage.book.id.eq(book.id)
                        .and(bookImage.isThumbnail.isTrue()));

        // 조회수를 가져오는 서브쿼리
        var viewCountSubquery = JPAExpressions.select(bookView.count())
                .from(bookView)
                .where(bookView.book.id.eq(book.id));

        // 리뷰 개수 가져오는 쿼리
        var reviewCountSubquery = JPAExpressions.select(review.count().intValue()) // int로 변환
                .from(review)
                .where(review.book.id.eq(book.id));

        // 평균 리뷰 점수 가져오는 서브쿼리
        var averageReviewRatingSubquery = JPAExpressions.select(review.reviewRating.avg())
                .from(review)
                .where(review.book.id.eq(book.id));

        // 정렬 기준 추출
        Sort.Order sortOrder = pageable.getSort().stream().findFirst().orElse(Sort.Order.asc("id")); // 기본 정렬 기준
        Expression<?> orderByExpression = switch (sortOrder.getProperty().toLowerCase()) {
            case "saleprice" -> book.salePrice;
            case "publishdate" -> book.publishDate;
            case "title" -> book.title;
            case "reviewrating" -> averageReviewRatingSubquery; // reviewRating에 대한 정렬 처리
            default -> book.id; // 기본값
        };

        // `reviewRating` 기준으로 정렬 시, 리뷰가 100개 이상인 책만 필터링하는 조건 추가
        BooleanExpression reviewCountCondition = (sortOrder.getProperty().equalsIgnoreCase("reviewrating"))
                ? reviewCountSubquery.goe(100) // 리뷰 개수가 100개 이상인 경우에만
                : null;

        // 제네릭 타입 명시적 설정
        @SuppressWarnings("unchecked")
        OrderSpecifier<?> orderSpecifier = sortOrder.getDirection().isDescending()
                ? new OrderSpecifier<>(Order.DESC, (Expression<Comparable>) orderByExpression)
                : new OrderSpecifier<>(Order.ASC, (Expression<Comparable>) orderByExpression);

        // bookDetail 가져오기
        List<BookSearchEntityDTO> content = from(book)
                .leftJoin(book.publisher, publisher)
                .where(titleCondition) // 제목 필터링 조건 추가
                .where(reviewCountCondition) // reviewCountCondition을 where 절에 추가
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
                        // 카테고리 아이디를 List로 변환
                        JPAExpressions.select(Expressions.stringTemplate(
                                        "GROUP_CONCAT({0})",
                                        category.id
                                ))
                                .from(bookCategory)
                                .leftJoin(bookCategory.category, category)
                                .where(bookCategory.book.id.eq(book.id)),
                        viewCountSubquery,
                        reviewCountSubquery, // 리뷰 갯수 추가
                        averageReviewRatingSubquery // 평균 리뷰 점수 추가
                ))
                .orderBy(orderSpecifier) // 정렬 기준 추가
                .fetch();

        log.debug("customImpl 메서드 값 확인 {}", content.toString());

        // Count query for pagination
        long total = from(book)
                .where(titleCondition) // 제목 필터링 조건 추가
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
                .where(bookImage.book.id.eq(book.id)
                        .and(bookImage.isThumbnail.isTrue()));


        // 조회수를 가져오는 서브쿼리
        var viewCountSubquery = JPAExpressions.select(bookView.count())
                .from(bookView)
                .where(bookView.book.id.eq(book.id));

        // 리뷰 개수
        var reviewCountSubquery = JPAExpressions.select(review.count().intValue()) // int로 변환
                .from(review)
                .where(review.book.id.eq(book.id));

        // 평균 리뷰 점수 가져오는 서브쿼리
        var averageReviewRatingSubquery = JPAExpressions.select(review.reviewRating.avg())
                .from(review)
                .where(review.book.id.eq(book.id));

        // 정렬 조건 설정
        Sort.Order sortOrder = pageable.getSort().stream().findFirst().orElse(Sort.Order.asc("title")); // 기본 정렬 기준
        Expression<?> orderByExpression = switch (sortOrder.getProperty().toLowerCase()) {
            case "saleprice" -> book.salePrice;
            case "publishdate" -> book.publishDate;
            case "title" -> book.title;
            case "reviewrating" -> averageReviewRatingSubquery; // 리뷰 평점으로 정렬
            default -> book.title; // 기본값
        };

        // 제네릭 타입 명시적 설정
        @SuppressWarnings("unchecked")
        OrderSpecifier<?> orderSpecifier = sortOrder.getDirection().isDescending()
                ? new OrderSpecifier<>(Order.DESC, (Expression<Comparable>) orderByExpression)
                : new OrderSpecifier<>(Order.ASC, (Expression<Comparable>) orderByExpression);

        // 메인 쿼리: 저자 이름 검색 및 도서 정보 조회
        BooleanBuilder whereBuilder = new BooleanBuilder();
        whereBuilder.and(Expressions.stringTemplate("LOWER({0})", author.name).like("%" + name.toLowerCase() + "%")); // 대소문자 구분 없이 저자 이름 조건

        // reviewrating으로 정렬할 때, 리뷰 개수가 100개 이상인 책만 가져오기
        if ("reviewrating".equalsIgnoreCase(sortOrder.getProperty())) {
            whereBuilder.and(reviewCountSubquery.goe(100)); // 리뷰 개수가 100개 이상인 책만 가져오기
        }

        List<BookSearchEntityDTO> content = from(book)
                .leftJoin(book.publisher, publisher)
                .leftJoin(bookAuthor).on(bookAuthor.book.id.eq(book.id))
                .leftJoin(bookAuthor.author, author)
                .leftJoin(bookAuthor.authorRole, authorRole)
                .where(whereBuilder) // 동적으로 where 조건 추가
                .groupBy(book.id, book.title, book.regularPrice, book.salePrice, book.publishDate, publisher.name)
                .orderBy(orderSpecifier) // 동적 정렬 조건 추가
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
                        // 카테고리 아이디를 List로 변환
                        JPAExpressions.select(Expressions.stringTemplate(
                                        "GROUP_CONCAT({0})",
                                        category.id
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
                .where(whereBuilder) // 동적으로 where 조건 추가
                .fetchCount();

        log.debug("BookRepositoryCustomImpl 확인:");

        // 페이지 처리된 결과 반환
        return new PageImpl<>(content, pageable, total);
    }




    // 특정 카테고리Id를 가진 책들을 반환.
    @Override
    public Page<BookSearchEntityDTO> findBooksByCategoryWithDetails(Long categoryId, Pageable pageable) {
        if (categoryId == null) {
            return emptyPage(pageable);
        }

        // 최하위 카테고리인지 확인하기 위한 서브쿼리
        var isLeafCategorySubquery = JPAExpressions.select(category.parent.id)
                .from(category)
                .where(category.id.eq(categoryId));

        // 최하위 카테고리일 경우 해당 카테고리만 가져오고, 그렇지 않으면 해당 카테고리와 하위 카테고리들을 모두 가져오기
        var categoryHierarchySubquery = JPAExpressions.select(category.id)
                .from(category)
                .where(
                        category.id.eq(categoryId)
                                .or(category.parent.id.eq(categoryId))
                                .or(category.id.eq(categoryId).and(category.parent.id.isNull()))
                )
                .distinct();

        // 리뷰 개수
        var reviewCountSubquery = JPAExpressions.select(review.count().intValue())
                .from(review)
                .where(review.book.id.eq(book.id));

        // 평균 리뷰 점수 가져오는 서브쿼리
        var averageReviewRatingSubquery = JPAExpressions.select(review.reviewRating.avg())
                .from(review)
                .where(review.book.id.eq(book.id));

        // 서브쿼리: 첫 번째 이미지 경로 가져오기
        var bookImagePathSubquery = JPAExpressions.select(bookImage.filePath)
                .from(bookImage)
                .where(bookImage.book.id.eq(book.id)
                        .and(bookImage.isThumbnail.isTrue()));



        // 조회수를 가져오는 서브쿼리
        var viewCountSubquery = JPAExpressions.select(bookView.count())
                .from(bookView)
                .where(bookView.book.id.eq(book.id));

        // 정렬 조건 설정
        Sort.Order sortOrder = pageable.getSort().stream().findFirst().orElse(Sort.Order.asc("title")); // 기본 정렬 기준
        Expression<?> orderByExpression = switch (sortOrder.getProperty().toLowerCase()) {
            case "saleprice" -> book.salePrice;
            case "publishdate" -> book.publishDate;
            case "title" -> book.title;
            case "reviewrating" -> averageReviewRatingSubquery;
            default -> book.title; // 기본값
        };

        // 제네릭 타입 명시적 설정
        @SuppressWarnings("unchecked")
        OrderSpecifier<?> orderSpecifier = sortOrder.getDirection().isDescending()
                ? new OrderSpecifier<>(Order.DESC, (Expression<Comparable>) orderByExpression)
                : new OrderSpecifier<>(Order.ASC, (Expression<Comparable>) orderByExpression);

        // 메인 쿼리: 카테고리와 자식 카테고리들을 포함하여 책들을 검색
        BooleanBuilder whereBuilder = new BooleanBuilder();
        whereBuilder.and(bookCategory.category.id.in(categoryHierarchySubquery)); // 카테고리 조건

        // 리뷰가 100개 이상인 책만 가져오기
        if ("reviewrating".equalsIgnoreCase(sortOrder.getProperty())) {
            whereBuilder.and(reviewCountSubquery.goe(100));
        }

        // 메인 쿼리: 책 검색
        List<BookSearchEntityDTO> content = from(book)
                .leftJoin(book.publisher, publisher)
                .leftJoin(bookCategory).on(bookCategory.book.id.eq(book.id))
                .leftJoin(bookCategory.category, category)
                .where(whereBuilder) // 동적으로 where 조건 추가
                .groupBy(book.id, book.title, book.regularPrice, book.salePrice, book.publishDate, publisher.name)
                .orderBy(orderSpecifier) // 정렬 조건 추가
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
                        // 카테고리 아이디를 List로 변환
                        JPAExpressions.select(Expressions.stringTemplate(
                                        "GROUP_CONCAT({0})",
                                        category.id
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
                .leftJoin(bookCategory).on(bookCategory.book.id.eq(book.id))
                .leftJoin(bookCategory.category, category)
                .where(whereBuilder) // 동적으로 where 조건 추가
                .fetchCount();

        // 페이지 처리된 결과 반환
        return new PageImpl<>(content, pageable, total);
    }


    @Override
    public BookDetailsDto findBookDetailsByBookId(Long bookId) {
        // 책 기본 정보 조회
        Book book = queryFactory
                .from(QBook.book)
                .leftJoin(QBook.book.publisher, publisher)
                .where(QBook.book.id.eq(bookId))
                .select(QBook.book)
                .fetchOne();


        // BookImage와 Review 데이터를 메서드를 통해 각각 가져오기
        List<BookImageDto> bookImages = findBookImagesByBookId(bookId);
        List<ReviewDto> reviews = findReviewsByBookId(bookId);

        List<String> tagNames = queryFactory
                .select(tag.name)
                .from(QBook.book)
                .join(QBook.book.bookTags, bookTag)
                .join(bookTag.tag, tag)
                .where(QBook.book.id.eq(bookId))
                .fetch();


        // 좋아요 개수 조회
        int likeCount = (int) queryFactory
                .from(like)
                .where(like.book.id.eq(bookId))
                .fetchCount();

        List<BookCategoryDto> categoryPathByBookId = findCategoryPathByBookId(bookId);

        return new BookDetailsDto(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getRegularPrice(),
                book.getSalePrice(),
                book.getExplanation(),
                book.getContents(),
                book.getPublishDate(),
                new PublisherDto(book.getPublisher().getId(), book.getPublisher().getName()),
                bookImages,
                reviews,
                tagNames,  // 태그 이름들
                likeCount,  // 좋아요 개수
                categoryPathByBookId,
                0L
        );
    }


    @Override
    public List<BookImageDto> findBookImagesByBookId(Long bookId) {
        return queryFactory
                .select(bookImage.filePath)
                .from(bookImage)
                .where(bookImage.book.id.eq(bookId))
                .fetch()
                .stream()
                .map(BookImageDto::new)  // String을 BookImageDto로 변환
                .collect(Collectors.toList());
    }


    @Override
    public List<ReviewDto> findReviewsByBookId(Long bookId) {


        List<String> reviewImageFilePaths = queryFactory
                .select(reviewImage.imageFilePath)
                .from(reviewImage)
                .join(reviewImage.review, review)
                .where(review.book.id.eq(bookId))
                .fetch();

        return queryFactory
                .from(review)
                .leftJoin(review.user, user)
                .where(review.book.id.eq(bookId))
                .select(Projections.constructor(
                        ReviewDto.class,
                        review.id,
                        review.reviewContent,
                        review.reviewRating,
                        review.reviewCreateAt,
                        user.name,
                        Expressions.constant(reviewImageFilePaths)
                ))
                .fetch();
    }

    @Override
    public List<BookCategoryDto> findCategoryPathByBookId(Long bookId) {
        // 1. Book ID로 연관된 Category 리스트 조회
        List<Category> categoryList = queryFactory
                .select(bookCategory.category)
                .from(bookCategory)
                .join(bookCategory.category, category)
                .where(bookCategory.book.id.eq(bookId))
                .fetch();

        // 2. 각 카테고리와 상위 관계를 CategoryDto로 변환
        Map<Long, BookCategoryDto> categoryMap = new HashMap<>();

        for (Category category : categoryList) {
            Category current = category;

            while (current != null) {
                categoryMap.putIfAbsent(
                        current.getId(),
                        new BookCategoryDto(
                                current.getId(),
                                current.getName(),
                                current.getDepth(),
//                                current.getDisplayOrder(),
                                new ArrayList<>()
                        )
                );
                current = current.getParent();
            }
        }

        // 3. 부모-자식 관계 설정
        List<BookCategoryDto> roots = new ArrayList<>();
        Set<Long> processedIds = new HashSet<>();

        for (Category category : categoryList) {
            Category current = category;

            while (current != null) {
                BookCategoryDto currentDto = categoryMap.get(current.getId());

                if (current.getParent() != null) {
                    BookCategoryDto parentDto = categoryMap.get(current.getParent().getId());
                    if (!parentDto.getChildren().contains(currentDto)) {
                        parentDto.getChildren().add(currentDto);
                    }
                } else if (!processedIds.contains(current.getId())) {
                    roots.add(currentDto); // 최상위 카테고리 추가
                    processedIds.add(current.getId());
                }

                current = current.getParent();
            }
        }

        // 4. 최상위 카테고리 리스트 반환
//        roots.sort(Comparator.comparingInt(BookCategoryDto::getDisplayOrder)); // 정렬 기준 설정
        return roots;
    }



}
