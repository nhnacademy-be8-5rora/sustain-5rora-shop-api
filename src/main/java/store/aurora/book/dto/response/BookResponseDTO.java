package store.aurora.book.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponseDTO {
    private Long id;
    private String title;
    private int regularPrice;
    private int salePrice;
    private boolean packaging;
    private Integer stock;
    private String explanation;
    private String contents;
    private String isbn;
    private LocalDate publishDate;
    private boolean isSale;
    private String publisherName;
    private String seriesName;

    private List<String> categories;
//    private List<String> tags;

    public BookResponseDTO(Long id, String title) {
        this.id = id;
        this.title = title;
    }
}