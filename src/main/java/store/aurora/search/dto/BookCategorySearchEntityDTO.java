package store.aurora.search.dto;


import lombok.NoArgsConstructor;
import lombok.Setter;
import store.aurora.book.dto.AuthorDTO;
import store.aurora.book.entity.AuthorRole;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Setter
public class BookCategorySearchEntityDTO {

    private Long id;
    private String title;
    private Integer regularPrice;
    private Integer salePrice;

    private LocalDate publishDate;

    private String publisherName;

    private List<AuthorDTO> authors; // 변경된 부분


    private String imgPath;
    private List<String> categoryNameList; // 카테고리 이름 리스트 추가

    public BookCategorySearchEntityDTO(Long id, String title, int regularPrice, int salePrice, LocalDate publishDate, String publisherName, String authorsString, String imgPath, String categoryNames) {
        this.id = id;
        this.title = title;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
        this.publishDate = publishDate;
        this.publisherName = publisherName;
        this.authors = convertAuthorsStringToList(authorsString); // 변환 로직
        this.imgPath = imgPath;
        this.categoryNameList = Arrays.asList(categoryNames.split(", ")); // 쉼표로 구분된 카테고리 이름을 List로 변환
    }
    // 쉼표로 구분된 문자열을 List<AuthorDTO>로 변환
    private List<AuthorDTO> convertAuthorsStringToList(String authorsString) {
        if (authorsString == null || authorsString.isEmpty()) {
            return List.of(); // 비어 있으면 빈 리스트 반환
        }

        return Arrays.stream(authorsString.split(", "))
                .map(author -> {
                    String[] parts = author.split(" \\("); // "이름 (역할)" 형태 분리
                    String name = parts[0];
                    String role = parts.length > 1 ? parts[1].replace(")", "") : null;

                    // 역할이 있을 경우, AuthorRole.Role로 변환
                    AuthorRole.Role authorRole = null;
                    if (role != null) {
                        try {
                            authorRole = AuthorRole.Role.valueOf(role);
                        } catch (IllegalArgumentException e) {
                            // 역할이 잘못된 값일 경우 예외 처리 (예: "Writer" -> "AUTHOR"로 대체 등)
                            authorRole = null;  // 또는 기본값을 지정할 수 있음
                        }
                    }

                    // AuthorDTO 생성 (role이 null일 수 있음)
                    return new AuthorDTO(name, authorRole);
                })
                .toList();
    }
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getRegularPrice() {
        return regularPrice;
    }
    public int getSalePrice() {
        return salePrice;
    }
    public LocalDate getPublishDate() {
        return publishDate;
    }
    public String getPublisherName() {
        return publisherName;
    }
   public List<AuthorDTO> getAuthors() {
        return authors;
   }
    public String getImgPath() {
        return imgPath;
    }
    public List<String> getCategoryNameList() {return categoryNameList;}
    @Override
    public String toString() {
        return "BookCategorySearchEntityDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", regularPrice=" + regularPrice +
                ", salePrice=" + salePrice +
                ", publishDate=" + publishDate +
                ", publisherName='" + publisherName + '\'' +
                ", authors=" + authors +
                ", imgPath='" + imgPath + '\'' +
                ", categoryNameList=" + categoryNameList +  // 카테고리 이름 목록 추가
                '}';
    }


}
