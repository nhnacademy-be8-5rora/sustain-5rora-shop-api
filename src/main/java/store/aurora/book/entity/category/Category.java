package store.aurora.book.entity.category;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonBackReference // 관계의 "inverse"
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference // 관계의 "owner"
    private List<Category> children = new ArrayList<>();

    @Column(nullable = false)
    private Integer depth;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCategory> bookCategories = new ArrayList<>();

    public void addChild(Category child) {
        children.add(child);
        child.setParent(this);
    }

    public void addBookCategory(BookCategory bookCategory) {
            bookCategories.add(bookCategory);
            bookCategory.setCategory(this);
    }

    // 카테고리에서 직접 책 리스트 조회
    public List<Book> getBooks() {
        return bookCategories.stream()
                .map(BookCategory::getBook)
                .toList();
    }
}
