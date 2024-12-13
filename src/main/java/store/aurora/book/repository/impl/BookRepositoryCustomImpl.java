package store.aurora.book.repository.impl;


import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import store.aurora.book.entity.Book;
import store.aurora.book.repository.BookRepositoryCustom;
import store.aurora.search.dto.BookSearchEntityDTO;

import static store.aurora.book.entity.QBook.book;
import static store.aurora.book.entity.QBookAuthor.bookAuthor;
import static store.aurora.book.entity.QPublisher.publisher;
import static store.aurora.book.entity.QAuthor.author;
import static store.aurora.book.entity.QAuthorRole.authorRole;
import static store.aurora.book.entity.QBookImage.bookImage;

import java.util.Collections;
import java.util.List;

public class BookRepositoryCustomImpl extends QuerydslRepositorySupport implements BookRepositoryCustom {


    public BookRepositoryCustomImpl() {
        super(Book.class);
    }


    @Override
    public Page<BookSearchEntityDTO> findBooksByTitleWithDetails(String title, Pageable pageable) {
        // 이미지를 하나만 가져오는 쿼리
        var bookImagePathSubquery = JPAExpressions.select(bookImage.filePath)
                .from(bookImage)
                .where(bookImage.book.id.eq(book.id))
                .orderBy(bookImage.id.asc())
                .limit(1);

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
                        bookImagePathSubquery
                ))
                .fetch();

        // Count query for pagination
        long total = from(book)
                .where(book.title.like("%" + title + "%"))
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }


    @Override
    public Page<BookSearchEntityDTO> findBooksByAuthorNameWithDetails(String name, Pageable pageable) {

        // 도서 ID 가져오기
        List<Long> bookIds = from(bookAuthor)
                .leftJoin(bookAuthor.author, author)
                .where(author.name.like("%" + name + "%"))
                .select(bookAuthor.book.id) // bookAuthor.book.id로 변경
                .fetch();

        // 도서 ID가 없는 경우 빈 페이지 반환
        if (bookIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 서브쿼리: 첫 번째 이미지만 가져오기
        var bookImagePathSubquery = JPAExpressions.select(bookImage.filePath)
                .from(bookImage)
                .where(bookImage.book.id.eq(book.id))
                .orderBy(bookImage.id.asc())
                .limit(1);

        // 도서 목록 가져오기
        List<BookSearchEntityDTO> content = from(book)
                .leftJoin(book.publisher, publisher)
                .where(book.id.in(bookIds))
                .groupBy(book.id, book.title, book.regularPrice, book.salePrice, book.publishDate, publisher.name)
                .orderBy(book.title.asc()) // 정렬 추가 (예: 제목 기준 오름차순)
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
                        bookImagePathSubquery
                ))
                .fetch();

        // 총 데이터 수 계산
        long total = from(book)
                .where(book.id.in(bookIds))
                .fetchCount();

        // 페이지 처리된 결과 반환
        return new PageImpl<>(content, pageable, total);
    }

}
