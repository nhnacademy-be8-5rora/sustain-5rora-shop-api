package store.aurora.book.entity.category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.aurora.book.entity.Book;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Column(nullable = false)
    private Integer depth;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCategory> bookCategories = new ArrayList<>();

    public void addBookCategory(BookCategory bookCategory) {
        if (!bookCategories.contains(bookCategory)) {
            bookCategories.add(bookCategory);
            bookCategory.setCategory(this);
        }
    }

    public void removeBookCategory(BookCategory bookCategory) {
        if (bookCategories.contains(bookCategory)) {
            bookCategories.remove(bookCategory);
            bookCategory.setCategory(null);
        }
    }

    // 카테고리에서 직접 책 리스트 조회
    public List<Book> getBooks() {
        return bookCategories.stream()
                .map(BookCategory::getBook)
                .toList();
    }
}
