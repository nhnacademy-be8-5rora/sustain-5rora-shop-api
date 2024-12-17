package store.aurora.search.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import store.aurora.book.repository.BookRepository;
import store.aurora.search.dto.BookCategorySearchEntityDTO;
import store.aurora.search.dto.BookCategorySearchResponseDTO;
import store.aurora.search.dto.BookSearchEntityDTO;
import store.aurora.search.dto.BookSearchResponseDTO;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SearchServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private SearchServiceImpl searchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("제목을 기준으로 검색할 때 데이터베이스에서 값을 가져오는 DTO(EntityDTO)를 반환해주는 DTO(ResponseDTO)로 잘 변환해주는지 확인")
    @Test
    void findBooksByTitleWithDetails_ValidTitle_MapsToResponseDTO() {
        // 반환받을 entityDTO 초기화
        String title = "Example Title";
        Pageable pageable = PageRequest.of(0, 8);

        BookSearchEntityDTO entityDTO = new BookSearchEntityDTO(1L, "Example Title", 20000, 18000,
                LocalDate.of(2022, 1, 1), "Example Publisher",
                "Author Name (AUTHOR)", "/images/example.jpg");
        Page<BookSearchEntityDTO> entityDTOPage = new PageImpl<>(Collections.singletonList(entityDTO));

        when(bookRepository.findBooksByTitleWithDetails(title, pageable)).thenReturn(entityDTOPage);

        // 정해둔 entityDTO 받기
        Page<BookSearchResponseDTO> result = searchService.findBooksByTitleWithDetails(title, pageable);

        // 결과
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        BookSearchResponseDTO responseDTO = result.getContent().get(0);
        assertThat(responseDTO.getId()).isEqualTo(1L);
        assertThat(responseDTO.getTitle()).isEqualTo("Example Title");
        assertThat(responseDTO.getRegularPrice()).isEqualTo(20000);
        assertThat(responseDTO.getSalePrice()).isEqualTo(18000);
        assertThat(responseDTO.getPublishDate()).isEqualTo(LocalDate.of(2022, 1, 1));
        assertThat(responseDTO.getPublisherName()).isEqualTo("Example Publisher");
        assertThat(responseDTO.getImgPath()).isEqualTo("/images/example.jpg");
        assertThat(responseDTO.getAuthors()).hasSize(1);
        assertThat(responseDTO.getAuthors().get(0).getName()).isEqualTo("Author Name");

        verify(bookRepository, times(1)).findBooksByTitleWithDetails(title, pageable);
    }

    @DisplayName("제목이 null 또는 blank 경우 빈페이지를 반환하는지 확인")
    @Test
    void findBooksByTitleWithDetails_NullOrEmptyTitle_ReturnsEmptyPage() {
        // 값 초기화
        String title = null;
        Pageable pageable = PageRequest.of(0, 8);

        String emptyTitle = "";

        // When
        Page<BookSearchResponseDTO> resultForNull = searchService.findBooksByTitleWithDetails(title, pageable);
        Page<BookSearchResponseDTO> resultForEmpty = searchService.findBooksByTitleWithDetails(emptyTitle, pageable);

        // Then
        assertThat(resultForNull).isNotNull();
        assertThat(resultForNull.getContent()).isEmpty();

        assertThat(resultForEmpty).isNotNull();
        assertThat(resultForEmpty.getContent()).isEmpty();

        verify(bookRepository, times(0)).findBooksByTitleWithDetails(title, pageable);
    }

    @DisplayName("작가 이름을 기준으로 검색할 때 데이터베이스에서 값을 가져오는 DTO(EntityDTO)를 반환해주는 DTO(ResponseDTO)로 잘 변환해주는지 확인")
    @Test
    void findBooksByAuthorNameWithDetails_ValidName_ReturnsPageOfBooks() {
        // 반환받을 값 초기화
        String authorName = "Example Author";
        Pageable pageable = PageRequest.of(0, 8);

        BookSearchEntityDTO entityDTO = new BookSearchEntityDTO(1L, "Example Title", 20000, 18000,
                LocalDate.of(2022, 1, 1), "Example Publisher",
                "Example Author (AUTHOR)", "/images/example.jpg");
        Page<BookSearchEntityDTO> entityDTOPage = new PageImpl<>(Collections.singletonList(entityDTO));

        when(bookRepository.findBooksByAuthorNameWithDetails(authorName, pageable)).thenReturn(entityDTOPage);

        // 반환받기
        Page<BookSearchResponseDTO> result = searchService.findBooksByAuthorNameWithDetails(authorName, pageable);

        // 결과
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        BookSearchResponseDTO responseDTO = result.getContent().get(0);
        assertThat(responseDTO.getAuthors()).hasSize(1);
        assertThat(responseDTO.getAuthors().get(0).getName()).isEqualTo("Example Author");

        verify(bookRepository, times(1)).findBooksByAuthorNameWithDetails(authorName, pageable);
    }

    @DisplayName("작가 이름이 null 또는 blank 일 경우 빈 페이지를 반환하는지 확인")
    @Test
    void findBooksByAuthorNameWithDetails_NullOrEmptyName_ReturnsEmptyPage() {
        String authorName = null;
        Pageable pageable = PageRequest.of(0, 8);

        String emptyAuthorName = "";

        // When
        Page<BookSearchResponseDTO> resultForNull = searchService.findBooksByAuthorNameWithDetails(authorName, pageable);
        Page<BookSearchResponseDTO> resultForEmpty = searchService.findBooksByAuthorNameWithDetails(emptyAuthorName, pageable);

        // Then
        assertThat(resultForNull).isNotNull();
        assertThat(resultForNull.getContent()).isEmpty();

        assertThat(resultForEmpty).isNotNull();
        assertThat(resultForEmpty.getContent()).isEmpty();

        verify(bookRepository, times(0)).findBooksByAuthorNameWithDetails(authorName, pageable);
    }

    @Test
    @DisplayName("카테고리 이름으로 책의 세부사항을 가져오는지 확인")
    public void testFindBooksByCategoryNameWithDetails() {
        // Given
        String categoryName = "Example Category";
        PageRequest pageable = PageRequest.of(0, 10); // 첫 번째 페이지, 10개의 결과

        // Mock 데이터 준비
        BookCategorySearchEntityDTO mockBook1 = new BookCategorySearchEntityDTO(
                1L, "Book Title 1", 1000, 800, LocalDate.now(), "Publisher 1", "Author1 (AUTHOR)", "imagePath1", "Example Category"
        );
        BookCategorySearchEntityDTO mockBook2 = new BookCategorySearchEntityDTO(
                2L, "Book Title 2", 1200, 1000, LocalDate.now(), "Publisher 2", "Author2 (EDITOR)", "imagePath2", "Example Category"
        );

        // Page 객체로 Mock 데이터 설정
        Page<BookCategorySearchEntityDTO> mockPage = new PageImpl<>(List.of(mockBook1, mockBook2), pageable, 2);

        // Mocking repository의 동작
        when(bookRepository.findBooksByCategoryNameWithDetails(categoryName, pageable)).thenReturn(mockPage);

        // When
        Page<BookCategorySearchResponseDTO> result = searchService.findBooksByCategoryNameWithDetails(categoryName, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(2); // Mock 데이터에 2개 책이 있으므로

        // BookCategorySearchResponseDTO 객체가 적절히 변환되었는지 확인
        result.getContent().forEach(book -> {
            assertThat(book.getTitle()).isNotNull(); // 도서 제목이 비어있지 않아야 함
            assertThat(book.getPublisherName()).isNotNull(); // 출판사 정보가 비어있지 않아야 함
            assertThat(book.getCategories()).isNotEmpty(); // 카테고리 목록이 비어있지 않아야 함
            assertThat(book.getCategories()).contains("Example Category"); // 카테고리 이름이 포함되어야 함
            assertThat(book.getAuthors()).isNotEmpty(); // 저자 목록이 비어있지 않아야 함
        });    }

    @DisplayName("카테고리 이름이 null 또는 blank 일 경우 빈 페이지를 반환하는지 확인")
    @Test
    void findBooksByCategoryNameWithDetails_NullOrEmptyName_ReturnsEmptyPage() {
        String categoryName = null;
        Pageable pageable = PageRequest.of(0, 8);

        String emptyCategoryName = "";

        // When
        Page<BookCategorySearchResponseDTO> resultForNull = searchService.findBooksByCategoryNameWithDetails(categoryName, pageable);
        Page<BookCategorySearchResponseDTO> resultForEmpty = searchService.findBooksByCategoryNameWithDetails(emptyCategoryName, pageable);

        // Then
        assertThat(resultForNull).isNotNull();
        assertThat(resultForNull.getContent()).isEmpty();

        assertThat(resultForEmpty).isNotNull();
        assertThat(resultForEmpty.getContent()).isEmpty();

        verify(bookRepository, times(0)).findBooksByAuthorNameWithDetails(categoryName, pageable);
    }
}
