package store.aurora.search.dto;


import lombok.NoArgsConstructor;
import lombok.Setter;
import store.aurora.book.dto.AuthorDTO;
import store.aurora.book.entity.AuthorRole;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // 정규식 기반으로 저자 데이터를 안전하게 분리
        return Arrays.stream(authorsString.split("\\s*,\\s*")) // 콤마와 공백 기준으로 분리
                .map(this::parseAuthor) // 각 저자 문자열을 AuthorDTO로 변환
                .toList();
    }

    private AuthorDTO parseAuthor(String authorString) {
        // 정규식으로 "이름 (역할)" 분리
        Pattern pattern = Pattern.compile("^(.*?)\\s*\\((.*?)\\)$");
        Matcher matcher = pattern.matcher(authorString);

        String name;
        String role = null;

        if (matcher.find()) {
            name = matcher.group(1).trim(); // 이름 추출
            role = matcher.group(2).trim(); // 역할 추출
        } else {
            // 역할이 없는 경우
            name = authorString.trim();
        }

        // 역할을 Enum으로 변환
        AuthorRole.Role authorRole = null;
        if (role != null && !role.isEmpty()) {
            try {
                authorRole = AuthorRole.Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid role: " + role + ", defaulting to null.");
                authorRole = null; // 잘못된 역할 처리
            }
        }

        return new AuthorDTO(name, authorRole);
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
