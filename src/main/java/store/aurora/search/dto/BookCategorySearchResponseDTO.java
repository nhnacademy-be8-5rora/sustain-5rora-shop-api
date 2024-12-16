package store.aurora.search.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.aurora.book.dto.AuthorDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BookCategorySearchResponseDTO {

    private Long id;
    private String title;
    private int regularPrice;
    private int salePrice;

    private LocalDate publishDate;

    private String publisherName;


    private String imgPath;

    private List<AuthorDTO> authors;

    private List<String> categories;

    public BookCategorySearchResponseDTO(BookCategorySearchEntityDTO book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.regularPrice = book.getRegularPrice();
        this.salePrice = book.getSalePrice();
        this.publishDate = book.getPublishDate();
        this.publisherName = book.getPublisherName();
        this.imgPath = book.getImgPath();

        this.authors = book.getAuthors();
        this.categories = book.getCategoryNameList();
    }

    @Override
    public String toString() {
        return "BookSearchResponseDTO [id=" + id + ", title=" + title + ", regularPrice=" + regularPrice +
                ", salePrice=" + salePrice + ", publishDate=" + publishDate + ", publisherName=" + publisherName +
                ", imgPath=" + imgPath + ", authors=" + authors + "]";
    }


}
