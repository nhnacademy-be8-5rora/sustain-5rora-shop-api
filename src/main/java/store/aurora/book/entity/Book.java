package store.aurora.book.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.aurora.book.entity.category.BookCategory;
import store.aurora.book.entity.category.Category;
import store.aurora.book.entity.tag.BookTag;
import store.aurora.book.entity.tag.Tag;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int regularPrice;

    @Column(nullable = false)
    private int salePrice;

    private Integer stock = 100;

    @Column(nullable = false)
    private boolean isSale;

    @Column(nullable = false, unique = true, length = 15)
    private String isbn;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String explanation;

    private boolean packaging = false;

    @Column(nullable = false)
    private LocalDate publishDate;

    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<BookImage> bookImages = new ArrayList<>();

    public void addBookImage(BookImage bookImage) {
        this.bookImages.add(bookImage);
        bookImage.setBook(this);
    }

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<BookCategory> bookCategories = new ArrayList<>();

    public void addBookCategory(BookCategory bookCategory) {
        bookCategories.add(bookCategory);
        bookCategory.setBook(this);
    }
    public void clearBookCategories() {
        // 모든 BookCategory와의 관계를 제거
        for (BookCategory bookCategory : bookCategories) {
            bookCategory.setBook(null); // 연관 관계를 해제
        }
        bookCategories.clear(); // 컬렉션 비우기
    }

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<BookTag> bookTags = new ArrayList<>();

    public void addBookTag(BookTag bookTag) {
        bookTags.add(bookTag);
        bookTag.setBook(this);
    }
    // 기존 태그 제거
    public void clearBookTags() {
        for (BookTag bookTag : bookTags) {
            bookTag.setBook(null); // 연관 관계 해제
        }
        bookTags.clear();
    }

}
