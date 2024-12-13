package store.aurora.book.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BookRequestDTO {

    @NotBlank
    private String title;

    @NotNull
    @Positive
    private int regularPrice;

    @NotNull
    @Positive
    private int salePrice;

    private boolean packaging = false;

    private Integer stock = 100;

    @NotBlank
    private String explanation;

    private String contents;

    private String isbn;

    @NotNull
    private LocalDate publishDate;

    @JsonProperty("isSale")
    @NotNull
    private boolean isSale;

    private List<Long> categoryIds;
    private List<Long> tagIds;

    @NotBlank
    private String publisherName;

    private String seriesName;
}